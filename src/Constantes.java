import java.util.Random;

public class Constantes {
    //Luego pasar a globales
    /**/
    public static int cantPixeles = 792;
    public static int cantPotreros = 1602;
    public static int cantEstaciones = 16;
    public static int cantAnios = cantEstaciones/4;
    public static int cantUsos= 15; //El cero esta reservado para inicio random
    public static int cantProductores= 42;
    public static int minimaCantidadUsos=3;
    public static int maximaCantidadUsos=6;

    //Maximo errores posibles para la funcion objetivo valor objetivo
    public static float maximoIncumplimientoFosforo=2.23F*cantAnios;
    public static float maximoIncumplimientoProductividadMinimaAnual=2100;
    public static float maximoIncumplimientoProductividadMinimaEstacion= 6000;
    public static float maximoIncumplimientoUsosDistintos=Constantes.cantEstaciones*Constantes.cantProductores;
    public static float pesoIncumplimientoFosforo=1;
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


    public static Uso[] usos = new Uso[cantUsos]; //el cero queda reservado para el no uso y el 14 cuando no se toca desde la ultima ves
    public static Pixel[] pixeles = new Pixel[cantPixeles];
    public static Productor[] productores= new Productor [cantProductores];
    public static int semilla=0;
    public static Random uniforme = new Random(semilla);




}




