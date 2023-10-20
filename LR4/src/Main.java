import mpi.*;

public class Main {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        long starTime, endTime;
        starTime = System.currentTimeMillis();

        int N = 100000;

        int[] A = new int[N];
        int[] B = new int[N];

        if (rank == 0) {
            initializeVectors(A, B, N);
        }

        // Рассылка векторов A и B на все процессы
        MPI.COMM_WORLD.Bcast(A, 0, N, MPI.INT, 0);
        MPI.COMM_WORLD.Bcast(B, 0, N, MPI.INT, 0);

        int localN = N / size;
        int[] localA = new int[localN];
        int[] localB = new int[localN];

        //распределения данных из корневого процесса на все процессы в коммуникаторе
        MPI.COMM_WORLD.Scatter(A, 0, localN, MPI.INT, localA, 0, localN, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(B, 0, localN, MPI.INT, localB, 0, localN, MPI.INT, 0);

        int[] localResult = new int[1];
        for (int i = 0; i < 1; i++)
            localResult[0] = calcLocalScalar(localA, localB, localN);

        int[] globalResult = new int[1];

        MPI.COMM_WORLD.Reduce(localResult, 0, globalResult, 0, 1, MPI.INT, MPI.SUM, 0);

        endTime = System.currentTimeMillis();

        if (rank == 0) {
            System.out.println("Итог: " + globalResult[0]);
            System.out.println("Программа закончила свою работу за " + (endTime - starTime) + " мс. и кол. процессов " + size);
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
