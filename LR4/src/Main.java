import mpi.MPI;
import mpi.Status;

public class Main {
    public static void main(String[] args) {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Размерность векторов
        int N = 1000; // Здесь можно указать нужное значение

        int[] a = new int[N];
        int[] b = new int[N];

        if (rank == 0) {
            // Инициализация векторов a и b (можно изменить на свой способ)
            for (int i = 0; i < N; i++) {
                a[i] = i + 1;
                b[i] = i + 2;
            }
        }

        int localN = N / size;
        int[] localA = new int[localN];
        int[] localB = new int[localN];

        // Рассылка частей векторов a и b
        MPI.COMM_WORLD.Scatter(a, 0, localN, MPI.INT, localA, 0, localN, MPI.INT, 0);
        MPI.COMM_WORLD.Scatter(b, 0, localN, MPI.INT, localB, 0, localN, MPI.INT, 0);

        // Локальное вычисление частичных скалярных произведений
        int localResult = 0;
        for (int i = 0; i < localN; i++) {
            localResult += localA[i] * localB[i];
        }

        int[] globalResult = new int[1];

        // Сбор частичных результатов
        MPI.COMM_WORLD.Reduce(localResult,0, globalResult, 0, 1, MPI.INT, MPI.SUM, 0);

        if (rank == 0) {
            System.out.println("Скалярное произведение: " + globalResult[0]);
        }

        MPI.Finalize();
    }
}
