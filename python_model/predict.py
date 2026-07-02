import os
import sys
import pickle
import numpy
import scipy.special
from PIL import Image


class neuralNetwork:
    def __init__(self, inputnodes, hiddennodes, outputnodes, learningrate):
        self.inodes = inputnodes
        self.hnodes = hiddennodes
        self.onodes = outputnodes
        self.lr = learningrate
        self.activation_function = lambda x: scipy.special.expit(x)

    def query(self, inputs_list):
        inputs = numpy.array(inputs_list, ndmin=2).T

        hidden_inputs = numpy.dot(self.wih, inputs)
        hidden_outputs = self.activation_function(hidden_inputs)

        final_inputs = numpy.dot(self.who, hidden_outputs)
        final_outputs = self.activation_function(final_inputs)

        return final_outputs


def preprocess_image(image_path):
    img = Image.open(image_path).convert("L")
    img = img.resize((28, 28))

    pixels = numpy.asarray(img, dtype=float)

    pixels = 255.0 - pixels

    inputs = (pixels.reshape(784) / 255.0 * 0.99) + 0.01

    return inputs


def main():
    if len(sys.argv) < 2:
        print("error,0")
        return

    image_path = sys.argv[1]

    current_dir = os.path.dirname(os.path.abspath(__file__))
    model_path = os.path.join(current_dir, "model.pkl")

    with open(model_path, "rb") as f:
        model_data = pickle.load(f)

    n = neuralNetwork(
        model_data["input_nodes"],
        model_data["hidden_nodes"],
        model_data["output_nodes"],
        model_data["learning_rate"]
    )

    n.wih = model_data["wih"]
    n.who = model_data["who"]

    inputs = preprocess_image(image_path)

    outputs = n.query(inputs)

    digit = int(numpy.argmax(outputs))
    confidence = float(outputs[digit][0])

    print(f"{digit},{confidence}")


if __name__ == "__main__":
    main()