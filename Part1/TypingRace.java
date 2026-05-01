import java.util.concurrent.TimeUnit;
import java.lang.Math;

public class TypingRace
{
    private int passageLength;   // Total characters in the passage to type
    private Typist seat1Typist;
    private Typist seat2Typist;
    private Typist seat3Typist;

    // Accuracy thresholds for mistype and burnout events
    // (Ty tuned these values "by feel". They may need adjustment.)
    private static final double MISTYPE_BASE_CHANCE = 0.3;
    private static final int    SLIDE_BACK_AMOUNT   = 2;
    private static final int    BURNOUT_DURATION     = 3;

    public TypingRace(int passageLength)
    {
        // just a small safety check so the race length is not 0 or negative
        if (passageLength < 1)
        {
            this.passageLength = 1;
        }
        else
        {
            this.passageLength = passageLength;
        }

        seat1Typist = null;
        seat2Typist = null;
        seat3Typist = null;
    }

    public void addTypist(Typist theTypist, int seatNumber)
    {
        if (seatNumber == 1)
        {
            seat1Typist = theTypist;
        }
        else if (seatNumber == 2)
        {
            seat2Typist = theTypist;
        }
        else if (seatNumber == 3)
        {
            seat3Typist = theTypist;
        }
        else
        {
            System.out.println("Cannot seat typist at seat " + seatNumber + " — there is no such seat.");
        }
    }

    public void startRace()
    {
        boolean finished = false;
        Typist winner = null;

        // Reset all typists to the start of the passage.
        // The original code missed seat3 and could crash if a seat was empty.
        if (seat1Typist != null)
        {
            seat1Typist.resetToStart();
        }

        if (seat2Typist != null)
        {
            seat2Typist.resetToStart();
        }

        if (seat3Typist != null)
        {
            seat3Typist.resetToStart();
        }

        // no point running the race if nobody has been added
        if (seat1Typist == null && seat2Typist == null && seat3Typist == null)
        {
            System.out.println("No typists have been added to the race.");
            return;
        }

        while (!finished)
        {
            // Advance each typist by one turn
            advanceTypist(seat1Typist);
            advanceTypist(seat2Typist);
            advanceTypist(seat3Typist);

            // Print the current state of the race
            printRace();

            // Check if any typist has finished the passage.
            // I check them one by one so I can store who won.
            if (raceFinishedBy(seat1Typist))
            {
                winner = seat1Typist;
                finished = true;
            }
            else if (raceFinishedBy(seat2Typist))
            {
                winner = seat2Typist;
                finished = true;
            }
            else if (raceFinishedBy(seat3Typist))
            {
                winner = seat3Typist;
                finished = true;
            }

            // Wait 200ms between turns so the animation is visible
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (Exception e) {}
        }

        // Task 2a: print the winner at the end
        if (winner != null)
        {
            System.out.println();
            System.out.println("And the winner is... " + winner.getName() + "!");
            System.out.println("Final accuracy: " + winner.getAccuracy());
        }
    }

    private void advanceTypist(Typist theTypist)
    {
        // empty seats are allowed, so just skip them
        if (theTypist == null)
        {
            return;
        }

        if (theTypist.isBurntOut())
        {
            // Recovering from burnout — skip this turn
            theTypist.recoverFromBurnout();
            return;
        }

        // Attempt to type a character
        if (Math.random() < theTypist.getAccuracy())
        {
            theTypist.typeCharacter();
        }

        // Mistype check — lower accuracy should mean a higher mistype chance
        if (Math.random() < (1.0 - theTypist.getAccuracy()) * MISTYPE_BASE_CHANCE)
        {
            theTypist.slideBack(SLIDE_BACK_AMOUNT);
        }

        // Burnout check — pushing too hard increases burnout risk
        // (probability scales with accuracy squared, capped at ~0.05)
        if (Math.random() < 0.05 * theTypist.getAccuracy() * theTypist.getAccuracy())
        {
            theTypist.burnOut(BURNOUT_DURATION);
        }
    }

    private boolean raceFinishedBy(Typist theTypist)
    {
        // empty seat cannot finish the race
        if (theTypist == null)
        {
            return false;
        }

        // >= is needed because progress can overshoot the exact finish number
        if (theTypist.getProgress() >= passageLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void printRace()
    {
        System.out.print('\u000C'); // Clear terminal

        System.out.println("  TYPING RACE — passage length: " + passageLength + " chars");
        multiplePrint('=', passageLength + 3);
        System.out.println();

        printSeat(seat1Typist);
        System.out.println();

        printSeat(seat2Typist);
        System.out.println();

        printSeat(seat3Typist);
        System.out.println();

        multiplePrint('=', passageLength + 3);
        System.out.println();
        System.out.println("  [zz] = burnt out    [<] = just mistyped");
    }

    private void printSeat(Typist theTypist)
    {
        // if there is no typist in the seat, print an empty lane instead of crashing
        if (theTypist == null)
        {
            System.out.print("| empty seat |");
            return;
        }

        int progress = theTypist.getProgress();

        // stops the display going past the finish line
        if (progress > passageLength)
        {
            progress = passageLength;
        }

        int spacesBefore = progress;
        int spacesAfter  = passageLength - progress;

        System.out.print('|');
        multiplePrint(' ', spacesBefore);

        // Always show the typist's symbol so they can be identified on screen.
        // Append ~ when burnt out so the state is visible without hiding identity.
        System.out.print(theTypist.getSymbol());
        if (theTypist.isBurntOut())
        {
            System.out.print('~');
            spacesAfter--; // symbol + ~ together take two characters
        }

        if (spacesAfter < 0)
        {
            spacesAfter = 0;
        }

        multiplePrint(' ', spacesAfter);
        System.out.print('|');
        System.out.print(' ');

        // Print name and accuracy
        if (theTypist.isBurntOut())
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")"
                + " BURNT OUT (" + theTypist.getBurnoutTurnsRemaining() + " turns)");
        }
        else
        {
            System.out.print(theTypist.getName()
                + " (Accuracy: " + theTypist.getAccuracy() + ")");
        }
    }

    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    // small main method so the text version can be run from the terminal
    public static void main(String[] args)
    {
        TypingRace race = new TypingRace(40);

        race.addTypist(new Typist('1', "TURBOFINGERS", 0.85), 1);
        race.addTypist(new Typist('2', "QWERTY_QUEEN", 0.60), 2);
        race.addTypist(new Typist('3', "HUNT_N_PECK", 0.30), 3);

        race.startRace();
    }
}
