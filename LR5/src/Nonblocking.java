import mpi.*;
import java.util.ArrayList;
import java.util.List;
public class Nonblocking {
    public static void main(String[] args) {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (size < 2) {
            System.out.println("This code should be run with at least two processes.");
            MPI.Finalize();
            return;
        }

        long startTime, endTime;
        startTime = System.currentTimeMillis();

        if (rank == 0) {
            // Generate the graph data on process with rank 0.
            GraphData graphData = generateGraphData();

            // Send data to all processes using blocking communication.
            GraphData[] dataToSend = new GraphData[]{graphData};
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Send(dataToSend, 0, 1, MPI.OBJECT, i, 0);
            }
        } else {
            // Processes with rank 1 and above receive data.
            GraphData[] dataReceived = new GraphData[1];
            MPI.COMM_WORLD.Recv(dataReceived, 0, 1, MPI.OBJECT, 0, 0);
            GraphData graphDataRecv = dataReceived[0];

            boolean isHypercube = checkIsomorphism(graphDataRecv);

            // Send the result to process with rank 0.
            byte[] sendBuffer = new byte[]{(byte) (isHypercube ? 1 : 0)};
            MPI.COMM_WORLD.Send(sendBuffer, 0, 1, MPI.BYTE, 0, 0);
        }

        if (rank == 0) {
            // Process with rank 0 collects the results.
            byte[] recvResults = new byte[size - 1];
            for (int i = 1; i < size; i++) {
                MPI.COMM_WORLD.Recv(recvResults, i - 1, 1, MPI.BYTE, i, 0);
            }

            boolean isHypercubeOverall = true;
            for (byte result : recvResults) {
                isHypercubeOverall &= (result != 0);
            }

            endTime = System.currentTimeMillis();

            if (isHypercubeOverall) {
                System.out.println("The graph is a hypercube.");
            } else {
                System.out.println("The graph is not a hypercube.");
            }

            System.out.println("Program completed in " + (endTime - startTime) + " ms with " + size + " processes.");
        }

        MPI.Finalize();
    }

    private static GraphData generateGraphData() {
        int numVertices = 10;
//        List<Edge> edges = new ArrayList<>();
//        edges.add(new Edge(0, 1));
//        edges.add(new Edge(0, 2));
//        edges.add(new Edge(1, 3));
//        edges.add(new Edge(2, 4));
//        edges.add(new Edge(3, 5));
//        edges.add(new Edge(4, 5));

        //return new GraphData(numVertices, edges);
        return generateHypercube(numVertices);
    }

    // функция для проверки изоморфизма гиперкубов
    private static boolean checkIsomorphism(GraphData data) {
        int numVertices = data.getNumVertices();
        List<Edge> edges = data.getEdges();

        // Проверяем, что количество вершин соответствует 2^n для некоторого n
        int n = 0;
        while (Math.pow(2, n) < numVertices) {
            n++;
        }

        if (Math.pow(2, n) != numVertices) {
            return false; // Количество вершин не является степенью двойки
        }

        // Проверяем, что граф соответствует гиперкубу
        for (Edge edge : edges) {
            int source = edge.getSource();
            int destination = edge.getDestination();

            int xor = source ^ destination;
            if (Integer.bitCount(xor) != 1) {
                return false; // Ребро не соединяет вершины с различающимися по одному биту кодами
            }
        }

        return true;
    }

    private static GraphData generateHypercube(int n) {
        int numVertices = (int) Math.pow(2, n);
        List<Edge> edges = new ArrayList<>();

        for (int i = 0; i < numVertices; i++) {
            for (int j = i + 1; j < numVertices; j++) {
                if (Integer.bitCount(i ^ j) == 1) {
                    edges.add(new Edge(i, j));
                }
            }
        }

        return new GraphData(numVertices, edges);
    }
}
