from flask import Flask, request, jsonify
from ultralytics import YOLO
import cv2
import requests
from datetime import datetime
import os

app = Flask(__name__)

# 🔥 Load models
vehicle_model = YOLO("yolov8n.pt")          # COCO model
helmet_model = YOLO("best.pt")       # Your trained model

UPLOAD_FOLDER = "uploads"
os.makedirs(UPLOAD_FOLDER, exist_ok=True)


# ✅ IoU function
def iou(boxA, boxB):
    xA = max(boxA[0], boxB[0])
    yA = max(boxA[1], boxB[1])
    xB = min(boxA[2], boxB[2])
    yB = min(boxA[3], boxB[3])

    interArea = max(0, xB - xA) * max(0, yB - yA)
    boxAArea = (boxA[2]-boxA[0]) * (boxA[3]-boxA[1])
    boxBArea = (boxB[2]-boxB[0]) * (boxB[3]-boxB[1])

    if (boxAArea + boxBArea - interArea) == 0:
        return 0

    return interArea / float(boxAArea + boxBArea - interArea)


@app.route('/detect', methods=['POST'])
def detect():
    if 'file' not in request.files:
        return jsonify({"error": "No file uploaded"}), 400

    file = request.files['file']
    filename = f"img_{int(datetime.now().timestamp())}.jpg"
    image_path = os.path.abspath(os.path.join(UPLOAD_FOLDER, filename))
    file.save(image_path)

    img = cv2.imread(image_path)
    if img is None:
        return jsonify({"error": "Invalid image"}), 400

    print("\n================ DEBUG START ================")

    # 🔹 Step 1: Detect vehicles + persons
    results = vehicle_model(img, verbose=False)

    person_boxes = []
    bike_boxes = []

    for r in results:
        for box in r.boxes:
            conf = float(box.conf[0])
            if conf < 0.4:
                continue

            cls = int(box.cls[0])
            label = vehicle_model.names[cls]
            coords = list(map(int, box.xyxy[0]))

            if label == "person":
                person_boxes.append(coords)
            elif label == "motorcycle":
                bike_boxes.append(coords)

    print("Persons:", len(person_boxes))
    print("Bikes:", len(bike_boxes))

    if not bike_boxes or not person_boxes:
        return jsonify({"warning": "No valid detections"}), 400

    # 🔹 Step 2: Find riders
    riders = []
    for p in person_boxes:
        for b in bike_boxes:
            overlap = iou(p, b)
            print("IoU Person-Bike:", overlap)
            if overlap > 0.1:
                riders.append(p)
                break

    print("Riders:", len(riders))

    if len(riders) == 0:
        return jsonify({"warning": "No riders found"}), 400

    # 🔹 Step 3: Helmet detection
    helmet_results = helmet_model(img, verbose=False)

    helmet_boxes = []

    print("\n---- HELMET DETECTIONS ----")

    for r in helmet_results:
        for box in r.boxes:
            conf = float(box.conf[0])
            cls = int(box.cls[0])
            label = helmet_model.names[cls]

            print("Detected:", label, "| Conf:", conf)

            if conf < 0.3:
                continue

            coords = list(map(int, box.xyxy[0]))

            if "With Helmet" in label:
                helmet_boxes.append(coords)

    print("Helmet Boxes:", len(helmet_boxes))

    # 🔹 Step 4: Match helmet to riders
    no_helmet_count = 0

    for rider in riders:
        has_helmet = False

        for h in helmet_boxes:
            overlap = iou(rider, h)
            print("IoU Rider-Helmet:", overlap)

            if overlap > 0.1:
                has_helmet = True
                break

        if not has_helmet:
            no_helmet_count += 1

    helmet_ok = no_helmet_count == 0

    print("No Helmet Count:", no_helmet_count)

    # 🔹 Step 5: Draw debug image
    debug_img = img.copy()

    for r in riders:
        cv2.rectangle(debug_img, (r[0], r[1]), (r[2], r[3]), (255, 0, 0), 2)

    for h in helmet_boxes:
        cv2.rectangle(debug_img, (h[0], h[1]), (h[2], h[3]), (0, 255, 0), 2)

    debug_path = image_path.replace(".jpg", "_debug.jpg")
    cv2.imwrite(debug_path, debug_img)

    print("Saved debug image:", debug_path)
    print("================ DEBUG END ================\n")

    # 🔹 Step 6: Prepare response
    reasons = []

    if len(riders) > 2:
        reasons.append("Triple Riding")

    if not helmet_ok:
        reasons.append(f"No Helmet ({no_helmet_count} rider(s))")

    data = {
        "vehicleType": "bike",
        "riderCount": len(riders),
        "helmet": helmet_ok,
        "numberPlate": "UNKNOWN",
        "imageUrl": image_path,
        "debugImage": debug_path,
        "timestamp": datetime.now().isoformat(),
        "reason": ", ".join(reasons) if reasons else "No violation"
    }

    print("📤 Sending:", data)

    # 🔥 ONLY FIX: Send to Spring Boot
    SPRING_URL = os.getenv("SPRING_BOOT_URL", "http://localhost:8080")

    try:
        response = requests.post(f"{SPRING_URL}/violations", json={
            "vehicleType": data["vehicleType"],
            "riderCount": data["riderCount"],
            "helmet": data["helmet"],
            "numberPlate": data["numberPlate"],
            "imageUrl": data["imageUrl"]
        })

        print("Spring Response:", response.status_code, response.text)

    except Exception as e:
        print("❌ Error sending to Spring:", e)

    return jsonify(data)


if __name__ == '__main__':
   app.run(host="0.0.0.0", port=5000)