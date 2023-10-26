import mpi.*;

public class Asynch {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        long startTime, endTime;
        startTime = System.currentTimeMillis();

        int N = 1000000;

        int[] A = new int[N];
        int[] B = new int[N];

        if (rank == 0) {
            initializeVectors(A, B, N);
        }

        // Асинхронная рассылка векторов A и B на все процессы
        final Thread bcastThreadA = new Thread(() -> MPI.COMM_WORLD.Bcast(A, 0, N, MPI.INT, 0));
        final Thread bcastThreadB = new Thread(() -> MPI.COMM_WORLD.Bcast(B, 0, N, MPI.INT, 0));

        bcastThreadA.start();
        bcastThreadB.start();

        // Ждем завершения асинхронных рассылок
        try {
            bcastThreadA.join();
            bcastThreadB.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int localN = N / size;
        int[] localA = new int[localN];
        int[] localB = new int[localN];

        // Асинхронное распределение данных из корневого процесса на все процессы в коммуникаторе
        final Thread scatterThreadA = new Thread(() -> MPI.COMM_WORLD.Scatter(A, 0, localN, MPI.INT, localA, 0, localN, MPI.INT, 0));
        final Thread scatterThreadB = new Thread(() -> MPI.COMM_WORLD.Scatter(B, 0, localN, MPI.INT, localB, 0, localN, MPI.INT, 0));

        scatterThreadA.start();
        scatterThreadB.start();

        // Ждем завершения асинхронных разделений
        try {
            scatterThreadA.join();
            scatterThreadB.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int[] localResult = new int[1];
        for (int i = 0; i < 1; i++)
            localResult[0] = calcLocalScalar(localA, localB, localN);

        int[] globalResult = new int[1];

        // Асинхронная редукция
        final Thread reduceThread = new Thread(() -> MPI.COMM_WORLD.Reduce(localResult, 0, globalResult, 0, 1, MPI.INT, MPI.SUM, 0));
        reduceThread.start();

        // Ждем завершения асинхронной редукции
        try {
            reduceThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();

        if (rank == 0) {
            System.out.println("Result: " + globalResult[0]);
            System.out.println("The program finished its work in " + (endTime - startTime) + " ms. and the number of processes " + size);
        }

        MPI.Finalize();
    }

    private static void initializeVectors(int[] A, int[] B, int N) {
        for (int i = 0; i < N; i++) {
            A[i] = (int) (Math.random() * 100);
            B[i] = (int) (Math.random() * 100);
        }
    }

    private static int calcLocalScalar(int[] A, int[] B, int N) {
        int result = 0;
        for (int i = 0; i < N; i++) {
            result += A[i] * B[i];
        }
        return result;
    }
}
