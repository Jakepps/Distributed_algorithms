import mpi.MPI;
import mpi.MPIException;

public class Main {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int TAG = 0;
        int s = 0;

        int buf = myrank;

        int nextRank = (myrank + 1) % size;
        int prevRank = (myrank - 1 + size) % size;

        if (myrank == 0) {
            int[] output = new int[]{0};
            MPI.COMM_WORLD.Sendrecv(new int[]{buf}, 0, 1, MPI.INT, nextRank, TAG,
                    output, 0, 1, MPI.INT, prevRank, TAG);

            System.out.println("Total sum: " + output[0]);
        } else {
            int[] output = new int[]{0};
            MPI.COMM_WORLD.Recv(output, 0, 1, MPI.INT, prevRank, TAG);

            s += output[0] + myrank;
            MPI.COMM_WORLD.Send(new int[]{s}, 0, 1, MPI.INT, nextRank, TAG);
        }

        MPI.Finalize();
    }
}
