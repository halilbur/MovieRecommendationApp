public class MaxHeap<T extends Comparable<T>> {
    //HeapNode class
    private static class HeapNode<T extends Comparable<T>> {
        T value;
        int userIndex;

        HeapNode(T value, int userIndex) {
            this.value = value;
            this.userIndex = userIndex;
        }
    }
    //MaxHeap class Attributes
    private HeapNode<T>[] heap;
    private int size;
    private int capacity;

    public MaxHeap(int capacity) {
        this.heap = new HeapNode[capacity];
        this.size = 0;
        this.capacity = capacity;
    }

    public int parent(int index) {
        return (index - 1) / 2;
    }

    public int getsize() {
        return size;
    }
    
    public int[] getUserId(int n) {
        int[] heapArray = new int[n];
        for (int i = 0; i < n; i++) {
            heapArray[i] = heap[i].userIndex;
        }
        return heapArray;
    }
    
    public T getMax() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty, no maximum element.");
        }

        return heap[0].value;
    }

    private void swap(int index1, int index2) {
        HeapNode<T> temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
    }

    private void heapifyUp(int index) {
        int parent = (index - 1) / 2;

        while (index > 0 && heap[index].value.compareTo(heap[parent].value) > 0) {
            swap(index, parent);
            index = parent;
            parent = (index - 1) / 2;
        }
    }

    private void heapifyDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int largest = index;

        if (leftChild < size && heap[leftChild].value.compareTo(heap[largest].value) > 0) {
            largest = leftChild;
        }

        if (rightChild < size && heap[rightChild].value.compareTo(heap[largest].value) > 0) {
            largest = rightChild;
        }

        if (largest != index) {
            swap(index, largest);
            heapifyDown(largest);
        }
    }
    //clear method
    public void clear() {
        heap = new HeapNode[capacity];
        size = 0;
    }
    public void insert(T value, int userIndex) {
        if (size >= capacity) {
            throw new IllegalStateException("Heap is full, cannot insert more elements.");

        }
        HeapNode<T> node = new HeapNode<>(value, userIndex);
        heap[size] = node;
        heapifyUp(size);
        size++;
    }

    public HeapNode<T> extractMax() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty, cannot extract maximum element.");
        }

        HeapNode<T> maxNode = heap[0];
        heap[0] = heap[size - 1];
        heapifyDown(0);
        size--;
        return maxNode;
    }


    void printByLevel() {

        for (int i = 0; i <= parent(size - 1); i++) {
            int level = (int) (Math.log(i + 1) / Math.log(2));

            System.out.println("Level:" + level);

            System.out.print("  Parent: " + heap[i].value);

            int left = 2 * i + 1;
            int right = 2 * i + 2;

            if (left < size) {
                System.out.print("\t\tLeft Child: " + heap[left].value);
            } else {
                System.out.print("\t\tLeft Child: - ");
            }

            if (right < size) {
                System.out.print("\t\tRight Child: " + heap[right].value);
            } else {
                System.out.print("\t\tRight Child: - ");
            }

            System.out.println("");
        }

    }

    public void printHeapToN(int N) {
        for (int i = 0; i < N; i++) {
            System.out.println("User Index: " + heap[i].userIndex + ", Value: " + heap[i].value);
        }
    }

    public static void main(String[] args) {
        MaxHeap<Integer> maxHeap = new MaxHeap<>(10);

        for (int j = 0; j < maxHeap.capacity; j++) {
            int cosineSimilarity = 5 + j;
            maxHeap.insert(cosineSimilarity, j);

        }
        maxHeap.printHeapToN(5);
        for (int i = 0; i < 5; i++) {
            System.out.println("get ıds: " + maxHeap.getUserId(5)[i]);
        }

    }

}
