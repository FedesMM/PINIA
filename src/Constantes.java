import java.util.Random;

public class Constantes {
    //Luego pasar a globales
    /**/
    public static final int cantPixeles = 792;
    public static final int cantPotreros = 1602;
    public static final int cantEstaciones = 16;
    public static final int cantAnios = cantEstaciones/4;
    public static final int cantUsos= 15; //El cero esta reservado para inicio random
    public static final int cantProductores= 42;
    public static int minimaCantidadUsos=3;
    public static int maximaCantidadUsos=6;

    //Maximo errores posibles para la funcion objetivo valor objetivo
    public static float maximoIncumplimientoFosforo=2.23F*cantAnios;
    public static float maximoIncumplimientoProductividadMinimaAnual=2100;
    public static float maximoIncumplimientoProductividadMinimaEstacion= 6000;
    public static float maximoIncumplimientoUsosDistintos=Constantes.cantEstaciones*Constantes.cantProductores;
    public static float pesoIncumplimientoFosforo=1;
    public static float pesoIncumplimientoProductividadMinimaAnual=1;
    public static float pesoIncumplimientoProductividadMinimaEstacion= 1;
    public static float pesoIncumplimientoUsosDistintos=1;




    /*
    //Constantes de test
    public static final int cantPixeles = 10;
    public static final int cantEstaciones = 12;
    public static final int cantAnios = cantEstaciones/4;
    public static final int cantUsos= 5;
    public static final int cantProductores= 2;
    public static int minimaCantidadUsos=2;
    public static int maximaCantidadUsos=4;
    */


    public static float[] topeFosforoAnual= new float[cantAnios];
    public static Uso[] usos = new Uso[cantUsos]; //el cero queda reservado para el no uso y el 14 cuando no se toca desde la ultima ves
    public static Pixel[] pixeles = new Pixel[cantPixeles];
    public static Productor[] productores= new Productor [cantProductores];
    public static int semilla=0;
    public static Random uniforme = new Random(semilla);





    //Cargar los datos de los usos
    //Cargar los datos de los pixeles
    public static void cargarTopeFosforoAnualTest() {
        for (int iAnio = 0; iAnio <cantAnios ; iAnio++) {
            Constantes.topeFosforoAnual[iAnio]=(cantAnios-iAnio)*600;
        }

    }

    public static void imprimirRestriccionesFosforoAnual(){
        System.out.print("\t\t Restriccion Fosforo Anual:\n\t\t\t{");
        for (int anio =0; anio < cantAnios;anio++){
            System.out.print(Constantes.topeFosforoAnual[anio]);
            if (anio!=(Constantes.topeFosforoAnual.length-1)){
                System.out.printf(", ");
            }
        }
        System.out.println("}");
    }
}




