
import java.io.*;
import java.util.*;

// This class is a Classifier that organizes and classifies data based
// on features and thresholds. It permits building a classifier from given
// input or labeled data, inserting new data, classifying new inputs, and
// maintaining its structure.
public class Classifier { 
    private ClassifierNode overallRoot;

    private static class ClassifierNode{
        public final String feature;
        public final double threshold;
        public final String label;
        public final TextBlock data;
        public ClassifierNode left;
        public ClassifierNode right;

        // Behavior: 
        //   - this method constructs a classifier based on feature and threshold and data.
        // Parameters:
        //   - feature: characteristics of our dataset that is used in classification
        //  and correspond to a numeric value.
        //   - threshold: comparsion numeric value
        //   - data: the data
        // Returns:
        //   - N/A
        // Exceptions:
        //   -N/A
        public ClassifierNode(String feature, double threshold, TextBlock data){
            this.feature = feature;
            this.threshold = threshold;
            this.data = data;
            this.label = null;
           
        }

        // Behavior: 
        //   - this method constructs the classifier with a classification label.
        // Parameters:
        //   - label: the classification label
        // Returns:
        //   - N/A
        // Exceptions:
        //   -N/A
        public ClassifierNode(String label, TextBlock data){
            this.feature = null;
            this.threshold = 0;
            this.label = label;
            this.data = data;
            
        }
    }
    
    // Behavior: 
    //   - this method constructs the classifier from the given input.
    // Parameters:
    //   - input: contains classifier data
    // Returns:
    //   - N/A
    // Exceptions:
    //   - if the given input is null, an IllegalArgumentException is thrown.
    public Classifier(Scanner input){
        if(input == null){
            throw new IllegalArgumentException();
        }
        overallRoot = ClassifierHelper(input);

    }

    // Behavior: 
    //   - this method builds the classifier from the given input.
    // Parameters:
    //   - input: contains classifier data
    // Returns:
    //   - ClassifierNode: the constructed classifier
    // Exceptions:
    //   - N/A
    private ClassifierNode ClassifierHelper(Scanner input){
        if(!input.hasNextLine()){
            return null;
        }
        String line = input.nextLine();
        if(line.startsWith("Feature: ")){
            String feature = line.substring(9);
            double threshold = Double.parseDouble(input.nextLine().substring(11));
            ClassifierNode temp = new ClassifierNode(feature, threshold, null);
            temp.left = ClassifierHelper(input);
            temp.right = ClassifierHelper(input);
            return temp;
        }
        else{
            return new ClassifierNode(line, null);
        }
    }


    // Behavior: 
    //   - this method constructs a classifier from labeled data.
    // Parameters:
    //   - data: the list of data
    //   - labels: list of corresponding labels for the data
    // Returns:
    //   - N/A
    // Exceptions:
    //   - if the given data and labels are null or the size of the data does not correspond
    //  to the size of the labels, or data is empty, an IllegalArgumentException is thrown.
    public Classifier(List<TextBlock> data, List<String> labels){
        if(data == null || labels == null || data.size()!= labels.size()|| data.isEmpty()){
            throw new IllegalArgumentException();
        }
        overallRoot = new ClassifierNode(labels.get(0), data.get(0));
        for(int i = 1; i < data.size(); i++){
           overallRoot = dataInserter(overallRoot, data.get(i), labels.get(i));
        }
    }


    // Behavior: 
    //   - this method inserts new data into the classifier.
    // Parameters:
    //   - node: current classifier position
    //   - data: the new data
    //   - label: the label for the new data
    // Returns:
    //   - ClassifierNode: the updated classifier with new data inserted
    // Exceptions:
    //   - N/A
    private ClassifierNode dataInserter(ClassifierNode node, TextBlock data, String label){
        if (node.label != null) {
            if (node.label.equals(label)) {
                return node;
            }
            String bestFeature = node.data.findBiggestDifference(data);
            double threshold = midpoint(node.data.get(bestFeature), data.get(bestFeature));
            ClassifierNode decisionNode = new ClassifierNode(bestFeature, threshold, node.data);
            if (data.get(bestFeature) < threshold) {
                decisionNode.left = new ClassifierNode(label, data);
                decisionNode.right = node; 
            } 
            else {
                decisionNode.right = new ClassifierNode(label, data);
                decisionNode.left = node;
            }
            return decisionNode;
        } 
        else {
            if (data.get(node.feature) < node.threshold) {
                node.left = dataInserter(node.left, data, label);
            } 
            else {
                node.right = dataInserter(node.right, data, label);
            }
            return node;
        }
    }


    // Behavior: 
    //   - this method classifies a given data input based on an existing classifier.
    // Parameters:
    //   - input: the data that will be classified 
    // Returns:
    //   - String: the classification label assigned to the data input
    // Exceptions:
    //   - if the given input is null, an IllegalArgumentException is thrown.
    public String classify(TextBlock input){
        if(input == null){
            throw new IllegalArgumentException();
        }
        return classifyHelper(overallRoot, input);
    }


    // Behavior: 
    //   - this method determines the classification label for a data input.
    // Parameters:
    //   - node: current classifier position 
    //   - input: the data that will be classified
    // Returns:
    //   - String: the classification label assigned to the data input
    // Exceptions:
    //   - N/A
    private String classifyHelper(ClassifierNode node, TextBlock input){
        if(node.label != null){
            return node.label;
        }
        if(input.get(node.feature) < node.threshold){
            return classifyHelper(node.left, input);
        }
        else{
            return classifyHelper(node.right, input);
        }
    }

    // Behavior: 
    //   - this method saves the structure of the classifier to a file.
    // Parameters:
    //   - output: prints the data of the classifier 
    // Returns:
    //   - N/A
    // Exceptions:
    //   - if the given output is null, an IllegalArgumentException is thrown.
    public void save(PrintStream output){
        if(output == null){
            throw new IllegalArgumentException();
        }
        saveHelper(overallRoot, output);
    }

    // Behavior: 
    //   - this method writes the structure of the classifier to a file.
    // Parameters:
    //   - node: current Classifier position 
    //   - output: prints the data of the classifier 
    // Returns:
    //   - N/A
    // Exceptions:
    //   - N/A
    private void saveHelper(ClassifierNode node, PrintStream output){
        if(node.label != null){
            output.println(node.label);
        }
        else{
            output.println("Feature: " + node.feature);
            output.println("Threshold: " + node.threshold);
            saveHelper(node.left, output);
            saveHelper(node.right, output);
        }
    }



    ////////////////////////////////////////////////////////////////////
    // PROVIDED METHODS - **DO NOT MODIFY ANYTHING BELOW THIS LINE!** //
    ////////////////////////////////////////////////////////////////////

    // Helper method to calcualte the midpoint of two provided doubles.
    private static double midpoint(double one, double two) {
        return Math.min(one, two) + (Math.abs(one - two) / 2.0);
    }    

    // Behavior: Calculates the accuracy of this model on provided Lists of 
    //           testing 'data' and corresponding 'labels'. The label for a 
    //           datapoint at an index within 'data' should be found at the 
    //           same index within 'labels'.
    // Exceptions: IllegalArgumentException if the number of datapoints doesn't match the number 
    //             of provided labels
    // Returns: a map storing the classification accuracy for each of the encountered labels when
    //          classifying
    // Parameters: data - the list of TextBlock objects to classify. Should be non-null.
    //             labels - the list of expected labels for each TextBlock object. 
    //             Should be non-null.
    public Map<String, Double> calculateAccuracy(List<TextBlock> data, List<String> labels) {
        // Check to make sure the lists have the same size (each datapoint has an expected label)
        if (data.size() != labels.size()) {
            throw new IllegalArgumentException(
                    String.format("Length of provided data [%d] doesn't match provided labels [%d]",
                                  data.size(), labels.size()));
        }
        
        // Create our total and correct maps for average calculation
        Map<String, Integer> labelToTotal = new HashMap<>();
        Map<String, Double> labelToCorrect = new HashMap<>();
        labelToTotal.put("Overall", 0);
        labelToCorrect.put("Overall", 0.0);
        
        for (int i = 0; i < data.size(); i++) {
            String result = classify(data.get(i));
            String label = labels.get(i);

            // Increment totals depending on resultant label
            labelToTotal.put(label, labelToTotal.getOrDefault(label, 0) + 1);
            labelToTotal.put("Overall", labelToTotal.get("Overall") + 1);
            if (result.equals(label)) {
                labelToCorrect.put(result, labelToCorrect.getOrDefault(result, 0.0) + 1);
                labelToCorrect.put("Overall", labelToCorrect.get("Overall") + 1);
            }
        }

        // Turn totals into accuracy percentage
        for (String label : labelToCorrect.keySet()) {
            labelToCorrect.put(label, labelToCorrect.get(label) / labelToTotal.get(label));
        }
        return labelToCorrect;
    }
}