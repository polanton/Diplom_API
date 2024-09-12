package steps;

import model.GetIngredientsResponse;
import model.objects.Ingredient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Generate {

    public  static String userMail(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        return "coolmail"+ timeStamp+"@mail.ru";
    }

    public  static String userName(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
        return "fancyname"+ timeStamp;
    }

    public static String password(){
        return "12345";
    }

    public static List<String> ingredientListForRequest(GetIngredientsResponse getIngredientsResponse){
        List<String> result = new ArrayList<>();
        int ingredientCount = ThreadLocalRandom.current().nextInt(1, 6);
        for (int i = 0 ;i< ingredientCount; i++){
            result.add(getIngredientsResponse.getRandomIngredientId());
        }
        return result;
    }
}
