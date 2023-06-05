public class CosineSimilarity {

    public static double computeCosineSimilarity(Double[] vectorA, Double[] vectorB) {
        if (vectorA.length != vectorB.length) {
            throw new IllegalArgumentException("Vectors must have the same length");
        }

        double dotProduct = 0;
        double normA = 0;
        double normB = 0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        double cosineSimilarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        return cosineSimilarity;
    }

    public static void main(String[] args) {
        int[] vectorA = { 1, 2, 3 };
        int[] vectorB = { 4, 5, 6 };


    }
}
