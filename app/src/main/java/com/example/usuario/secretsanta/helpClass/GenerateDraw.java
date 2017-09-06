package com.example.usuario.secretsanta.helpClass;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.usuario.secretsanta.R;

public class GenerateDraw{

    private Context context;
    private String nameGroup;
    private String []lstNames, origin, destiny;
    private OperateDBParticipants db;
    private PreferencesExclusions exclusions;

    public GenerateDraw(Context context, String nameGroup)
    {
        this.context = context;
        this.nameGroup = nameGroup;
        lstNames = null;

        //Get all participants
        db = new OperateDBParticipants(context, nameGroup, null, 1);
        db.openDB();
        lstNames = db.getData();
        db.closeDB();

        //Get the names of exclusions
        exclusions = new PreferencesExclusions(context);
    }

    public boolean makeDraw(String []data)
    {
        String [][]how_gift = new String[2][lstNames.length];
        String []email = new String[how_gift[0].length];
        boolean value = false;

        if (exclusions.getNumberExclusions() == 0)
        {
            //No exclusions...
            how_gift = noExclusions();
        }
        else
        {
            //With exclusions...
            origin = exclusions.getOrigin();
            destiny = exclusions.getDestiny();
            how_gift = withExclusions();
        }
        if (how_gift != null)
        {
            //Call method send E-mails
            value = sendData(how_gift, data);
        }
        if (value == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public String[][] comp_exclusions()
    {
        String [][]how_gift = new String[2][lstNames.length];
        //If exclusions...
        origin = exclusions.getOrigin();
        destiny = exclusions.getDestiny();
        how_gift = withExclusions();

        if (how_gift != null)
        {
            return how_gift;
        }
        else
        {
            return null;
        }
    }

    public String[][] noExclusions()
    {
        //Method with no exclusions
        String [][]how_gift = new String[2][lstNames.length];
        int []gifted = new int[lstNames.length];
        boolean flag = true;

        for (int i=0; i<lstNames.length; i++)//Each element of the list
        {
            flag = true;
            for (int j=0; flag == true; j++)
            {
                int x = (int)(Math.random()*(lstNames.length));

                if (lstNames[i].equals(lstNames[x]))//If choose the same...
                {
                    //Gifting him/herself
                }
                else if (gifted[x] == 1)//If it is already gifted
                {
                    //Already has a 'gift'
                }
                else//If choose an element without gift yet...
                {
                    gifted[x] = 1;
                    how_gift[0][i] = "" + lstNames[i];
                    how_gift[1][i] = "" + lstNames[x];
                    flag = false;
                }
            }
        }
        return how_gift;
    }

    public String[][] withExclusions()
    {
        String []list = new String[lstNames.length];
        String [][]how_gift = new String[2][lstNames.length];
        int []gifted = new int[lstNames.length];
        boolean flag = true;

        list = sortExclusions();

        for (int i=0; i<list.length; i++)//Participants who gifted
        {
            if (gifted[i] == 0)//If its not chosen put '2' cos it cannot chose itself
            {
                gifted[i] = 2;
            }
            //Add the exclusions, put '2'
            gifted = exclude(gifted, i, list);

            if (gifted != null)//Draw
            {
                flag = true;
                for (int j=0; flag == true; j++)
                {
                    int x = (int)(Math.random()*(lstNames.length));

                    if (list[i].equals(list[x]))//Itself
                    {

                    }
                    else if (gifted[x] == 1)//Already chosen
                    {

                    }
                    else if (gifted[x] == 2)//If its part of an exclusion
                    {

                    }
                    else//If we can make the draw
                    {
                        gifted[x] = 1;
                        how_gift[0][i] = "" + list[i];
                        how_gift[1][i] = "" + list[x];
                        flag = false;
                    }
                }
                //Take out the exclusions
                for (int j=0; j<gifted.length; j++)
                {
                    if (gifted[j] == 2)
                    {
                        gifted[j] = 0;
                    }
                }
            }
            else
            {
                //total = false;
                Toast.makeText(context, R.string.dialog_cannot_make_excl, Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return how_gift;
    }


    public int[] exclude(int []gifted, int i, String []list)//Pass vector to put '2' and the index to mark participant
    {
        for(int t=0; t<origin.length; t++)
        {
            if (list[i].equals(origin[t]))//If the name is in origin at least has one exclusion...
            {
                String name = destiny[t];//Copy the destiny associated
                //Toast.makeText(context, "No a "+nombre,Toast.LENGTH_SHORT).show();
                for(int j=0; j<list.length; j++)//Check the position to who cannot be gifted
                {
                    if (list[j].equals(name))
                    {
                        //Toast.makeText(context, "Est� en la pos "+(j+1),Toast.LENGTH_SHORT).show();
                        if (gifted[j] == 1)
                        {
                            //Dejamos en 1 porque no hace falta, ya que de todas maneras no podr� regalar
                        }
                        else if (gifted[j] == 0)
                        {
                            //Si es cero a�n puede alguien regalarle con lo cual ponemos un 2 para que no
                            //pueda ser regalado por origen[i] y a su vez luego podamos quitar la exclusi�n
                            gifted[j] = 2;
                        }
                    }
                }
            }
        }

        //Check validity of exclusions
        boolean possible = false;
        for (int j=0; j<gifted.length; j++)
        {
            if (gifted[j] == 0)//If exists one element at 0 we can do the draw...
            {
                possible = true;
            }
        }

        if (possible)
        {
            return gifted;
        }
        else//If not participants to be chosen we return null and we cannot make the draw
        {
            return null;
        }
    }

    public String[] sortExclusions()
    {
        //Si ordenamos la lista y ponemos a los participantes con mas exclusiones delante, le daremos
        //prioridad para que elijan a quien regalar, lo que nos evitará casos de posibles errores por
        //falta de participantes válidos en un determinado punto de el sorteo
        String []list = new String[lstNames.length];
        int []number = new int[lstNames.length];


        for (int i=0; i<lstNames.length; i++)
        {
            for (int j=0; j<origin.length; j++)
            {
                if (lstNames[i].equals(origin[j]))//Vemos si esta en origen(tiene exclusi�n) y contamos cuantas tiene
                {
                    number[i]++;//Contamos el n�mero de exclusiones de cada elemento
                }
            }
            list[i] = "" + lstNames[i];//Copiamos lista_nombres en lista
        }

        for (int i=0; i<(number.length-1); i++)//Ordenamos la lista de los nombres
        {
            int max = i;//Cogemos como mayor el numero de la lista que corresponda
            for (int j = i+1 ; j<number.length ; j++) //Buscamos si existe otro mayor hacia adelante
            {
                if (number[j] > number[max])
                {
                    max = j;//Si hay uno mayor, cogemos su �ndice para permutarlo en la posici�n de i
                }
            }

            if (i != max)
            {
                //Permutamos los valores para la lista de el numero de exclusiones y de los nombres
                int aux = number[i];
                number[i] = number[max];
                number[max] = aux;

                String aux1 = list[i];
                list[i] = list[max];
                list[max] = aux1;
            }
        }
        return list;
    }

    public boolean sendData(String [][]how_gift, String []data)
    {
        String []email = new String[how_gift[0].length];

        db.openDB();
        for (int i=0; i<how_gift[0].length; i++)
        {
            Mail m = new Mail("amigoinvisibleesp@gmail.com", "amigoinvisible", this.context);//Email address and password real
            email[i] = db.getEmail(how_gift[0][i]);

            String[] toArr = {email[i]};
            m.setTo(toArr);
            m.setFrom("amigoinvisibleesp@gmail.com");
            m.setSubject("Secret Santa");
            m.setBody("¡Hello "+how_gift[0][i]+"! You gift will go to: "+how_gift[1][i]+"\n"+
                    "The event called "+data[0]+" will be celebrated in "+data[1]+" on the "+
                    data[2]+" at "+ data[3]+"\nThe theme will be: "+data[4]+
                    " and the range of prices: "+data[5]);
            try
            {
                if(m.send())
                {

                }
                else
                {
                    Toast.makeText(context, R.string.error_send_email, Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (Exception e)
            {
                Toast.makeText(context, R.string.error_data, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        db.closeDB();
        return true;
    }
}
