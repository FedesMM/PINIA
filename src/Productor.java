import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Productor {
    int numeroProductor;
    float[] restriccionProduccionEstacion;
    float[] restriccionProduccionAnual;
    List<Integer> pixelesDelProductor;
    float areaTotal;


    public Productor(int numeroProductor, float[] restriccionProduccionEstacion, float[] restriccionProduccionAnual, List<Integer> pixelesDelProductor) {
        this.numeroProductor = numeroProductor;
        this.restriccionProduccionEstacion = restriccionProduccionEstacion;
        this.restriccionProduccionAnual = restriccionProduccionAnual;
        this.pixelesDelProductor = pixelesDelProductor;
        this.areaTotal=0;
    }

    public static Productor[] cargarProductores(){
        float[] restriccionProductorE, restriccionProductorA;
        List<Integer> pixelesDelProductor;
        Productor[] productores = new Productor[Constantes.cantProductores];
        //El productor cero tiene todos los pixeles pares
        restriccionProductorE= new float[] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        restriccionProductorA= new float[] {0,0,0,0};
        pixelesDelProductor= new ArrayList<Integer>();


        //PRUEBA SIN RESTRICCIONES PARA EL PRODUCTOR 1
        productores[0]= new Productor(0,restriccionProductorE, restriccionProductorA,pixelesDelProductor );
        for (int iProductores = 1; iProductores < Constantes.cantProductores; iProductores++) {
            //El productor cero tiene todos los pixeles pares
            restriccionProductorE= new float[] {1200,600,2100,2100,1200,600,2100,2100,1200,600,2100,2100,1200,600,2100,2100};
            restriccionProductorA= new float[] {6000,6000,6000,6000};
            pixelesDelProductor= new ArrayList<Integer>();
            productores[iProductores]= new Productor(iProductores,restriccionProductorE, restriccionProductorA,pixelesDelProductor );
        }

        /*
        for (int iProductores = 0; iProductores < Constantes.cantProductores; iProductores++) {
            //El productor cero tiene todos los pixeles pares
            restriccionProductorE= new float[] {1200,600,2100,2100,1200,600,2100,2100,1200,600,2100,2100,1200,600,2100,2100};
            restriccionProductorA= new float[] {6000,6000,6000,6000};
            pixelesDelProductor= new ArrayList<Integer>();
            productores[iProductores]= new Productor(iProductores,restriccionProductorE, restriccionProductorA,pixelesDelProductor );
        }
        */

        return productores;
    }

    public static Productor[] cargarProductoresTest(){
        Productor[] productores = new Productor[Constantes.cantProductores];

        //El productor cero tiene todos los pixeles pares
        float[] restriccionProductorE0= new float[] {1200,600,2100,2100,1200,600,2100,2100,1200,600,2100,2100};
        float[] restriccionProductorA0= new float[] {6000,6000,6000};
        productores[0]= new Productor(0,restriccionProductorE0, restriccionProductorA0,Arrays.asList(2,4));
        //El productor uno tiene todos los pixeles inpares
        float[] restriccionProductorE1= new float[] {1200,600,2100,2100,1200,600,2100,2100,1200,600,2100,2100};
        float[] restriccionProductorA1= new float[] {6000,6000,6000};
        productores[1]= new Productor(0,restriccionProductorE1, restriccionProductorA1,Arrays.asList(1,3));
        return productores;
    }
    public void imprimirProductor(){
        System.out.printf("("+this.numeroProductor+",{");
        /*
        for (int i = 0; i <this.restriccionProduccionEstacion.length ; i++) {
            System.out.print(this.restriccionProduccionEstacion[i]);
            if (i!=(this.restriccionProduccionEstacion.length-1)){
                System.out.printf(",");
            }
        }*/
        System.out.printf("},{");
        for (int i = 0; i <this.pixelesDelProductor.size(); i++) {
            System.out.print(this.pixelesDelProductor.get(i));
            if (i!=(this.pixelesDelProductor.size()-1)) {
                System.out.printf(",");
            }
        }
        System.out.println("})");
    }
    public static void imprimirProductores(){
        for (int i = 0; i < Constantes.productores.length; i++) {
            Constantes.productores[i].imprimirProductor();
        }
    }

}
