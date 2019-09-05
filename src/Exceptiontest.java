import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Exceptiontest {


    public static void main(String[] args) {

//        try {
//
//            System.out.println("1");
//
//            throw new NullPointerException();
//        } catch (NullPointerException e) {
//            System.out.println("2");
//            throw new NullPointerException();
//        } catch (NumberFormatException e) {
//            System.out.println("3");
//        } finally {
//            System.out.println("4");
//        }

        List<Integer> List = Arrays.asList(2,5,7,90,12,56);
        List = List.stream().sorted(Integer::compareTo).collect(Collectors.toList());
        int size = List.size();
        System.out.println(List.get(size-1)+" "+List.get(size-2));

    }
}
