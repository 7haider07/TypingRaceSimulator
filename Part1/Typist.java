public class Typist
{
    // basic details for the typist
    private char symbol;
    private String name;

    // progress is how many characters they have typed correctly so far
    private int progress;

    // true when the typist is frozen for a few turns
    private boolean burntOut;

    // this stores how many burnout turns are still left
    private int burnoutTurnsRemaining;

    // accuracy is stored between 0.0 and 1.0
    private double accuracy;


    // Constructor of class Typist
    public Typist(char typistSymbol, String typistName, double typistAccuracy)
    {
        // set the simple starting values
        symbol = typistSymbol;
        name = typistName;
        progress = 0;
        burntOut = false;
        burnoutTurnsRemaining = 0;

        // using the setter here means the accuracy gets checked straight away
        setAccuracy(typistAccuracy);
    }


    // Methods of class Typist

    public void burnOut(int turns)
    {
        // if turns is 0 or less, there is no point burning them out
        if (turns <= 0)
        {
            burntOut = false;
            burnoutTurnsRemaining = 0;
        }
        else
        {
            burntOut = true;
            burnoutTurnsRemaining = turns;
        }
    }

    public void recoverFromBurnout()
    {
        // only count down if they are actually burnt out
        if (burntOut == true)
        {
            burnoutTurnsRemaining = burnoutTurnsRemaining - 1;

            // once it hits 0, the burnout is finished
            if (burnoutTurnsRemaining <= 0)
            {
                burntOut = false;
                burnoutTurnsRemaining = 0;
            }
        }
    }

    public double getAccuracy()
    {
        return accuracy;
    }

    public int getProgress()
    {
        return progress;
    }

    public String getName()
    {
        return name;
    }

    public char getSymbol()
    {
        return symbol;
    }

    public int getBurnoutTurnsRemaining()
    {
        return burnoutTurnsRemaining;
    }

    public void resetToStart()
    {
        // new race, so start from the beginning again
        progress = 0;
        burntOut = false;
        burnoutTurnsRemaining = 0;
    }

    public boolean isBurntOut()
    {
        return burntOut;
    }

    public void typeCharacter()
    {
        // extra check here so a burnt out typist cannot move by accident
        if (burntOut == false)
        {
            progress = progress + 1;
        }
    }

    public void slideBack(int amount)
    {
        progress = progress - amount;

        // stops progress becoming negative
        if (progress < 0)
        {
            progress = 0;
        }
    }

    public void setAccuracy(double newAccuracy)
    {
        // clamp the value so it always stays in the valid range
        if (newAccuracy < 0.0)
        {
            accuracy = 0.0;
        }
        else if (newAccuracy > 1.0)
        {
            accuracy = 1.0;
        }
        else
        {
            accuracy = newAccuracy;
        }
    }

    public void setSymbol(char newSymbol)
    {
        symbol = newSymbol;
    }
}
