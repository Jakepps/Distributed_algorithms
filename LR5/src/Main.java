import mpi.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (size < 2) {
            System.out.println("Этот код должен быть запущен как минимум на двух процессах.");
            MPI.Finalize();
            return;
        }

        if (rank == 0) {
            // Генерируем исходные данные (граф) на процессе с рангом 0.
            GraphData graphData = generateGraphData();

            // Создаем массив для хранения статусов отправки.
            Status[] sendStatus = new Status[size - 1];
            Request[] sendRequests = new Request[size - 1];

            // Отправляем данные всем процессам неблокирующим способом.
            for (int i = 1; i < size; i++) {
                sendRequests[i - 1] = MPI.COMM_WORLD.Isend(graphData, 0, 1, MPI.INT, i, 0);
            }

            for (int i = 0; i < size - 1; i++) {
                sendStatus[i] = sendRequests[i].Wait();
            }
        } else {
            // Процессы с рангами 1 и выше принимают данные.
            GraphData graphDataRecv = new GraphData();

            // Создаем массив для хранения статусов приема.

            MPI.COMM_WORLD.Irecv(graphDataRecv, 0, 1, MPI.OBJECT, 0, 0).Wait();

            boolean isHypercube = checkIsomorphism(graphDataRecv);

            // Отправляем результат на процесс с рангом 0.
            MPI.COMM_WORLD.Isend(isHypercube, 0, 1, MPI.BOOLEAN, 0, 0);
        }

        if (rank == 0) {
            // Процесс с рангом 0 собирает результаты.
            boolean[] results = new boolean[size - 1];

            // Создаем массив для хранения статусов приема результатов.
            Status[] recvStatus = new Status[size - 1];
            Request[] recvRequests = new Request[size - 1];

            for (int i = 1; i < size; i++) {
                recvRequests[i - 1] = MPI.COMM_WORLD.Irecv(results, i - 1, 1, MPI.BOOLEAN, i, 0);
            }

            // Ждем завершения всех операций приема результатов.
            for (int i = 0; i < size - 1; i++) {
                recvStatus[i] = recvRequests[i].Wait();
            }

            boolean isHypercubeOverall = true;
            for (boolean result : results) {
                isHypercubeOverall &= result;
            }

            if (isHypercubeOverall) {
                System.out.println("Граф является гиперкубом.");
            } else {
                System.out.println("Граф не является гиперкубом.");
            }
        }

        MPI.Finalize();
    }

    private static GraphData generateGraphData() {
        int numVertices = 6;
        List<Edge> edges = new ArrayList<>();
        edges.add(new Edge(0, 1));
        edges.add(new Edge(0, 2));
        edges.add(new Edge(1, 3));
        edges.add(new Edge(2, 4));
        edges.add(new Edge(3, 5));
        edges.add(new Edge(4, 5));

        GraphData gr1 = generateHypercube(numVertices);

        return new GraphData(numVertices, edges);
        //return gr1;
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
