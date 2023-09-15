import mpi.MPI;
import mpi.MPIException;
import mpi.Request;


public class Main {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int TAG = 0;

        int[] data = new int[size];
        int[] filteredData = new int[size];

        // Инициализация массива данных
        for (int i = 0; i < size; i++) {
            data[i] = i * 2; // Здесь вы можете установить свое условие фильтрации
        }

        // Рассчитываем, сколько элементов будет удовлетворять условию фильтрации
        int filteredCount = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] % 4 == 0) { // Пример условия: оставляем только четные элементы
                filteredData[filteredCount] = data[i];
                filteredCount++;
            }
        }

        // Создаем массив для принимаемых данных
        int[] receivedData = new int[filteredCount];

        // Создаем массивы запросов для неблокирующих операций
        Request[] sendRequests = new Request[size];
        Request[] recvRequests = new Request[size];

        // Неблокирующая отправка данных от каждого процесса
        for (int i = 0; i < size; i++) {
            sendRequests[i] = MPI.COMM_WORLD.Isend(new int[]{filteredData[i]}, 0, 1, MPI.INT, i, TAG);
        }

        // Неблокирующий прием данных
        for (int i = 0; i < size; i++) {
            recvRequests[i] = MPI.COMM_WORLD.Irecv(new int[]{receivedData[i]}, 0, 1, MPI.INT, i, TAG);
        }

        // Ждем завершения всех неблокирующих операций
        Request.Waitall(sendRequests);
        Request.Waitall(recvRequests);

        // Выводим отфильтрованные данные на каждом процессе
        System.out.println("Process " + rank + " received filtered data: ");
        for (int i = 0; i < filteredCount; i++) {
            System.out.print(receivedData[i] + " ");
        }
        System.out.println();

        MPI.Finalize();
    }
}
