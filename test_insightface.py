import cv2
import numpy as np
import pickle
import insightface
from insightface.app import FaceAnalysis
from numpy.linalg import norm
'''
# Initialize the InsightFace model
app = FaceAnalysis(name='buffalo_l')  # High-accuracy model
app.prepare(ctx_id=0)  # Remove the 'nms' parameter
'''
app = FaceAnalysis(name='buffalo_l')  # Use lighter "buffalo_s" model
app.prepare(
    ctx_id=-1,  # Required for Intel GPUs (disables CUDA)
    
)
# Load known faces from a file (if exists)
try:
    with open("known_faces.pkl", "rb") as f:
        known_faces = pickle.load(f)
except FileNotFoundError:
    known_faces = {}

# Function to compute cosine similarity
def cosine_similarity(embedding1, embedding2):
    return np.dot(embedding1, embedding2) / (norm(embedding1) * norm(embedding2))

# Function to recognize a face
def recognize_face(embedding, threshold=0.5):
    best_match = None
    best_score = 0
    for name, known_embedding in known_faces.items():
        score = cosine_similarity(embedding, known_embedding)
        if score > best_score and score > threshold:
            best_match = name
            best_score = score
    return best_match, best_score

# Open the camera
cap = cv2.VideoCapture(0)  # Change to 1 for an external webcam

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break

    rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

    # Detect faces
    faces = app.get(rgb_frame)

    for face in faces:
        bbox = face.bbox.astype(int)
        x1, y1, x2, y2 = bbox
        embedding = face.normed_embedding  # Extract the face embedding

        # Try to recognize the face
        name, confidence = recognize_face(embedding)

        # If face is known, display name
        if name:
            label = f"{name} ({confidence:.2f})"
            color = (0, 255, 0)
        else:
            label = "Unknown"
            color = (0, 0, 255)

        # Draw bounding box and label
        cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)
        cv2.putText(frame, label, (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)

    # Show the frame
    cv2.imshow("3D Face Recognition", frame)

    key = cv2.waitKey(1) & 0xFF
    if key == ord('q'):
        break
    elif key == ord('s'):
        # Save a new face embedding
        name = input("Enter name: ")
        if faces:
            known_faces[name] = faces[0].normed_embedding
            with open("known_faces.pkl", "wb") as f:
                pickle.dump(known_faces, f)
            print(f"Face saved as {name}!")

# Cleanup
cap.release()
cv2.destroyAllWindows()
