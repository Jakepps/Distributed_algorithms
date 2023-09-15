import mpi.MPI;
import mpi.MPIException;
import mpi.Status;

//Probe позволяет узнать о характеристиках сообщения ДО того,
//как сообщение будет помещено в приемный пользовательский буфер

public class Main {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int data[] = new int[1];
        int buf[] = {1, 3, 5};
        int back_buf[] = new int[10];
        int back_buf2[] = new int[10];
        int count, TAG = 0;
        Status st;

        data[0] = 2016;
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (rank == 0) {
            MPI.COMM_WORLD.Send(data, 0, 1, MPI.INT, 2, TAG);
        } else if (rank == 1) {
            MPI.COMM_WORLD.Send(buf, 0, buf.length, MPI.INT, 2, TAG);
        } else if (rank == 2) {
            st = MPI.COMM_WORLD.Probe(0, TAG);
            count = st.Get_count(MPI.INT);
            MPI.COMM_WORLD.Recv(back_buf, 0, count, MPI.INT, 0, TAG);
            System.out.print("Rank = 0 ");
            for (int i = 0; i < count; i++)
                System.out.print(back_buf[i] + " ");

            st = MPI.COMM_WORLD.Probe(1, TAG);
            count = st.Get_count(MPI.INT);
            MPI.COMM_WORLD.Recv(back_buf2, 0, count, MPI.INT, 1, TAG);
            System.out.print("Rank = 1 ");
            for (int i = 0; i < count; i++)
                System.out.print(back_buf2[i] + " ");
        }

        MPI.Finalize();
    }
}
