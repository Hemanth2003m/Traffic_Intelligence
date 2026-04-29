# Base image
FROM python:3.10-slim

# Set working directory
WORKDIR /app

# Prevent Python from buffering logs
ENV PYTHONUNBUFFERED=1

# Install minimal system dependencies required for OpenCV
RUN apt-get update && apt-get install -y \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements first (enables Docker cache)
COPY requirements.txt .

# Upgrade pip (helps avoid install issues)
RUN pip install --upgrade pip

# Install PyTorch (CPU version, faster and stable)
RUN pip install --no-cache-dir --prefer-binary \
    torch torchvision torchaudio \
    --index-url https://download.pytorch.org/whl/cpu

# Install remaining dependencies
RUN pip install --no-cache-dir --prefer-binary -r requirements.txt

# Copy application code
COPY . .

# Create uploads directory
RUN mkdir -p uploads

# Expose Flask port
EXPOSE 5000

# Run the application
CMD ["python", "app.py"]
