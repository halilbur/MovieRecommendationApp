import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

// ...

public class CSVRead {

    private static Hashtable<Integer, Double[]> data = new Hashtable<>();
    private static Hashtable<String, Integer> movieData = new Hashtable<>();
    private static Hashtable<Integer, Double[]> targetUserData = new Hashtable<>();
    private static Hashtable<Integer, String> movieNameData = new Hashtable<>();

    static {
        loadMovieData();
        loadUserData();
        loadTargetUser_data();
    }

    private static MaxHeap<Double> maxHeap = new MaxHeap<>(data.size());
    private static int[] mostSimiliarUserIds;
    private static final String mainDataFile = "/main_data.csv"; // "data.csv
    private static final String moviesFile = "/movies.csv"; // "movies.csv
    private static final String targetUserFile = "/target_user.csv"; // "target_user.csv

    public static BufferedReader getBufferedReader(String fileName) {
        return new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(CSVRead.class.getResourceAsStream(fileName))));
    }

    //getting user from target user file by id and return vector
    public static void loadTargetUser_data() {
        try (CSVReader reader = new CSVReader(getBufferedReader(targetUserFile))) {
            List<String[]> rows = reader.readAll();
            String[] headers = rows.get(0); // Get the header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                Double[] vector = new Double[row.length - 1];
                for (int j = 1; j < row.length; j++) {
                    vector[j - 1] = Double.parseDouble(row[j]);
                }
                int userId = Integer.parseInt(row[0]); // Assuming the first column contains user IDs
                targetUserData.put(userId, vector);
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    private static void loadMovieData() {
        try (CSVReader reader = new CSVReader(getBufferedReader(moviesFile))) {
            String[] nextLine;
            boolean isFirstLine = true; // Flag to skip the first line (headers)
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                int id = Integer.parseInt(nextLine[0]);
                String name = nextLine[1];
                movieData.put(name, id);
                movieNameData.put(id, name);
            }
        } catch (IOException | NumberFormatException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private static void loadUserData() {
        try (CSVReader reader = new CSVReader(getBufferedReader(mainDataFile))) {
            List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) { // Start from index 1 to skip the first row
                String[] row = rows.get(i);
                Double[] vector = new Double[row.length - 1];
                for (int j = 1; j < row.length; j++) { // Start from index 1 to skip the first column
                    vector[j - 1] = Double.parseDouble(row[j]);
                }
                data.put(i - 1, vector); // Adjust index by subtracting 1 to match the data index
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    //get movie id by name
    public static int getMovieIdByName(String movieName) {
        Integer movieId = movieData.get(movieName);
        if (movieId != null) {
            return movieId;
        } else {
            System.out.println("Movie name not found.");
            return -1;
        }
    }

    public static String getMovieNameById(int movieId) {
        String movieName = movieNameData.get(movieId);
        if (movieName != null) {
            return movieName;
        } else {
            System.out.println("Movie ID not found.");
            return null;
        }
    }

    public static int[] getTopRatedMoviesForUser(int userId, int X) {
        MaxHeap<Double> maxHeap = new MaxHeap<>(9019);
        Double[] userVector = data.get(userId - 1);
        if (userVector == null) {
            throw new IllegalArgumentException("User ID not found in the data.");
        }
        for (int i = 0; i < userVector.length; i++) {
            maxHeap.insert(userVector[i], i + 1);
        }
        int[] topRatedMovies = maxHeap.getUserId(X);
        maxHeap.clear();
        return topRatedMovies;

    }

    public static void loopDataAndInsertHeap(Double[] givenVector) {
        for (int i = 0; i < data.size(); i++) {
            Double[] vector = data.get(i);
            double cosineSimilarity = CosineSimilarity.computeCosineSimilarity(vector, givenVector);
            maxHeap.insert(cosineSimilarity, i + 1);
        }
    }

    public static int[] calculateCosineSimilarityBetweenUsers(Double[] userVector, int K) {
        loopDataAndInsertHeap(userVector);
        mostSimiliarUserIds = maxHeap.getUserId(K);
        maxHeap.clear();
        return mostSimiliarUserIds;
    }

    public static int[] calculateCosineSimilarityBetweenUsers(int target_userid, int K) {
        Double[] target_vector = returnUserVector(target_userid);
        if (target_vector == null) {
            throw new IllegalArgumentException("User ID not found in the data.");
        }

        loopDataAndInsertHeap(target_vector);

        mostSimiliarUserIds = maxHeap.getUserId(K);
        maxHeap.clear();
        return mostSimiliarUserIds;
    }

    public static Double[] returnUserVector(int target_userID) {
        Double[] targetUserVector = targetUserData.get((target_userID));
        if (targetUserVector != null) {
            return targetUserVector;
        } else {
            System.out.println("User ID not found.");
            return null;
        }
    }
}
