package ec.util;
import ec.vector.IntegerMatrixSpecies;

public class MatrixUtils {

    public static Range getRangeOfSeasonInPixel(int i) {
        int cantSeasons = IntegerMatrixSpecies.CANT_ESTACIONES;
        int ini = (i / cantSeasons) * cantSeasons;
        int fin = ini + cantSeasons;

        return new Range(ini, fin);
    }

    public static int getClosestIntSeason(int i) {
        Range range = getRangeOfSeasonInPixel(i);
        if (i - range.ini < range.fin - i) {
            return range.ini;
        } else {
            return range.fin;
        }
    }

    public static Range pullApartIntsInSeason(int i, int j) {
        Range rangei = getRangeOfSeasonInPixel(i);
        Range rangej = getRangeOfSeasonInPixel(j);
        return new Range(rangei.ini, rangej.ini);
    }
}
