import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from tensorflow.keras import layers
from tensorflow.keras.models import Sequential
import os
import librosa
import librosa.display

BATCH_SIZE = 100
ITERATIONS = 500
ITERATIONS_TEST = 10
EVAL_EVERY = 5
HEIGHT = 20
WIDTH = 130
NUM_LABELS = 0
LABEL_TO_INDEX_MAP = {}

os.listdir(os.getcwd())
testing_path = os.path.join(os.getcwd(), 'test')
training_path = os.path.join(os.getcwd(), 'train')

labels = os.listdir(training_path)
index = 0
for label in labels:
    LABEL_TO_INDEX_MAP[label] = index
    index += 1
    NUM_LABELS = len(LABEL_TO_INDEX_MAP)

print(LABEL_TO_INDEX_MAP)


def mfcc_get(path_files):
    wave, sr = librosa.load(path_files, mono=True)
    mfccs = librosa.feature.mfcc(y=wave, sr=sr, n_mfcc=HEIGHT)
    return mfccs


X = []
y = []

for i in labels:
    training_folders = os.path.join(training_path, i)
    for a in os.listdir(training_folders):
        training_audio = os.path.join(training_folders, a)
        X.append(mfcc_get(training_audio))
        encoding = [0] * len(LABEL_TO_INDEX_MAP)
        encoding[LABEL_TO_INDEX_MAP[i]] = 1
        y.append(encoding)

plt.figure(figsize=(12,4))
plt.title("Mel Frequency Cepstral Coefficients")
librosa.display.specshow(np.array(X)[1], x_axis ="time")
plt.colorbar()

X_test = []
y_test = []

testing_path = 'C:\\Users\\Nikita\\PycharmProjects\\pythonProject\\test'
print(LABEL_TO_INDEX_MAP)
for i in os.listdir(testing_path):
    if ".txt" in i:
        file = open(os.path.join(testing_path, i), "r")
        f = file.readlines()
        labels = list(map(str.strip, f))
        string_labels = ''.join(labels)
        encoding2 = [0] * len(LABEL_TO_INDEX_MAP)
        for label in labels:
            encoding2[LABEL_TO_INDEX_MAP[label]] = 1
        y_test.append(encoding2)

    else:
        testing_audio = os.path.join(testing_path, i)
        wave, sr = librosa.load(testing_audio, mono=True)
        mfccs_test = librosa.feature.mfcc(y=wave, sr=sr, n_mfcc=150)
        mfccs_test = np.pad(mfccs_test, ((0, 0), (0, 975 - len(mfccs_test[0]))), mode="constant")
        X_test.append(mfccs_test)

test_data = np.array(X_test)
test_labels = tf.argmax(np.array(y_test), axis=1)

print(test_data.shape)
print(test_labels.shape)
plt.figure(figsize=(12, 4))
plt.title("Mel Frequency Cepstral Coefficients")
librosa.display.specshow(test_data[1], x_axis="time")
plt.colorbar()

train_data = np.array(X)
train_labels = np.array(y)
train_data = np.random.random(size=(6705, 20, 130))
train_data = np.expand_dims(train_data, axis=3)
train_labels = tf.argmax(train_labels, axis=1)
print(train_data.shape, train_labels.shape)

model = Sequential()
model.add(layers.Conv2D(32, (3, 3), activation='relu', input_shape=train_data.shape[1:]))
model.add(layers.MaxPool2D(2, 2))
model.add(layers.Conv2D(32, (3, 3), activation='relu'))
model.add(layers.MaxPool2D(2, 2))
model.add(layers.Flatten())
model.add(layers.Dense(128, activation="relu"))
model.add(layers.Dense(34, activation="relu"))
model.add(layers.Dense(NUM_LABELS))

model.summary()

np.random.shuffle(train_data)
shuffled_train_labels = tf.random.shuffle(train_labels)
np.random.shuffle(test_data)
shuffled_test_labels = tf.random.shuffle(test_labels)
test_data_ed = np.expand_dims(test_data, axis=3)

model.compile(optimizer="adam", loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True), metrics=["accuracy"])
model.fit(train_data, shuffled_train_labels, epochs=30, validation_data=(test_data_ed, shuffled_test_labels))
model.save('my_model.h5')
