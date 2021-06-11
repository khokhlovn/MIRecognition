import os
import librosa
import numpy as np
import tensorflow as tf
import operator
from shutil import copyfile

uncat_path = os.path.join(os.getcwd(), 'unc_music')
training_path = os.path.join(os.getcwd(), 'train')
cat_path = os.path.join(os.getcwd(), 'cat_music')
labels = os.listdir(training_path)
LABEL_TO_INDEX_MAP = {}
index = 0

for label in labels:
    LABEL_TO_INDEX_MAP[label] = index
    index += 1

model = tf.keras.models.load_model('my_model.h5')
model.compile(optimizer="adam", loss=tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True), metrics=["accuracy"])

for i in os.listdir(uncat_path):
    if ".wav" in i:
        audio = os.path.join(uncat_path, i)
        wave, sr = librosa.load(audio, mono=True)
        mfccs_test = librosa.feature.mfcc(y=wave, sr=sr, n_mfcc=150)
        mfccs_test = np.pad(mfccs_test, ((0, 0), (0, 975 - len(mfccs_test[0]))), mode="constant")
        classes = model.predict(mfccs_test, batch_size=10)
        genresMap = {'cou_fol': 0, 'cla': 0, 'pop-roc': 0, 'lat-sou': 0}
        listed = list(LABEL_TO_INDEX_MAP.keys())
        path = ''

        iter = 0
        for c in classes:
            if c == 1:
                solo = listed[iter]
                s = solo.split('][')[1]
                genre = s.replace(']', '')
                genresMap[genre] += 1
                path += solo.split('][')[0].replace('[', '')
            iter += 1

        detected_genre = max(genresMap.iteritems(), key=operator.itemgetter(1))[0]
        os.mkdir(detected_genre)
        os.mkdir(path)
        to_save_path = os.path.join(os.getcwd(), detected_genre + '\\' + path)
        copyfile(i, to_save_path)
