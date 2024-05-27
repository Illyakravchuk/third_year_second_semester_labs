import java.util.concurrent.BrokenBarrierException;

public class T2 extends Thread {
    private Data data;
    final int id = 2;
    private int a2;
    private int d2;
    private int p2;
    public T2(Data d) {
        data = d;
    }

    @Override
    public void run() {
        System.out.println("T2 is started");

        try {
            data.MD = data.initializeVectorWithOnes(data.MD);
            data.d = data.initializeVariableToOne(data.d);

            // Сигнал задачам T1, T3, T4 про введення MD, d і чекати введення даних в задачах T1, T3, T4 - бар'єр B1
            data.B1.await();

            int indexStart = (id - 1) * data.H;
            int indexEnd =  id * data.H;
            int [][] MCh = Data.subMatrix(data.MC, indexStart, indexEnd);

            // Обчислення 1
            int []Xh = Data.vectorMatrixMultiplication(data.R, MCh);
            for (int i = indexStart; i < indexEnd; i++) {
                data.X[i] += Xh[i - indexStart];
            }
            // Обчислення 2
            int []Bh = Data.subVector(data.B,indexStart, indexEnd);
            int []Zh = Data.subVector(data.Z,indexStart, indexEnd);

            a2 = data.scalarProduct(Bh, Zh);

            // доступ до спільного ресурсу - КД1
            // Обчислення 3
            data.a.updateAndGet(current -> current + a2);          // атомік-змінна a
            // сигнал про завершення обчислення
            data.S2.release(3);      // семафор S2
            // очікування на завершення обчислень а з Т1, Т3, Т4
            data.S1.acquire();             // семафор S1
            data.S3.acquire();             // семафор S3
            data.S4.acquire();             // семафор S4

            int [][] MDh = Data.subMatrix(data.MD, indexStart, indexEnd);
            // Обчислення 4
            int []Fh = Data.vectorMatrixMultiplication(data.X, MDh);
            int []Eh = Data.subVector(data.E,indexStart, indexEnd);

            // копіювання p2 = p --КД2 семафор S6
            data.S6.acquire();
            p2 = data.p;
            data.S6.release();

            // копіювання a2 = a --КД3 критична секція CS1
            a2 = data.copy_a_CS1();

            // копіювання d2 = d --КД4 семафор S7
            data.S7.acquire();
            d2 = data.d;
            data.S7.release();

            //Обчислення 5 : Aн= Fн * p + a * Eн * d
            int minLen = Math.min(Fh.length, Eh.length);
            int[] Ah = new int[minLen];

            for (int i = 0; i < minLen; i++) {
                Ah[i] = Fh[i] * p2 + a2 * Eh[i] * d2;
            }

            for (int i = indexStart; i < indexEnd; i++) {
                data.A[i] += Ah[i - indexStart];
            }
            // Сигнал про завершення обчислення A семафор S5
            data.S5.release();

        } catch (InterruptedException | BrokenBarrierException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("T2 is finished");
        }
    }
}