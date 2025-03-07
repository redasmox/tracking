'''import cv2
from ultralytics import YOLO

# Load the YOLOv11 model
model = YOLO("yolo11n.pt")  # Make sure the YOLOv11 weights file is available

# Initialize webcam (0 is default webcam)
cap = cv2.VideoCapture(0)

while True:
    # Read frame from the webcam
    ret, frame = cap.read()
    
    if not ret:
        print("Failed to grab frame")
        break
    
    # Perform object detection on the frame
    results = model(frame)
    
    # Render the results on the frame (returns a list of images, so use the first one)
    frame_with_boxes = results[0].plot()  # Correctly access the rendered image
    
    # Display the frame with detections
    cv2.imshow("YOLOv11 Object Detection", frame_with_boxes)
    
    # Break the loop when 'q' is pressed
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# Release the webcam and close windows
cap.release()
cv2.destroyAllWindows()
'''
from ultralytics import YOLO

# Load a COCO-pretrained YOLO11n model
model = YOLO("yolo11n.pt")

# Train the model on the COCO8 example dataset for 100 epochs
#results = model.train(data="coco8.yaml", epochs=100, imgsz=640)
# Run inference with the YOLO11n model on the 'bus.jpg' image
#results = model("C:\Users\ADDOU\Desktop\bmodels\screen-capture.webm", save=True, show=True)
results = model(r"C:\Users\ADDOU\Desktop\bmodels\screen-capture.webm", save=True, show=True)
