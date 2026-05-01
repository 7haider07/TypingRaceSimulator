import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TypingRaceGUI
{
    private JFrame frame;
    private JTextArea raceArea;
    private JTextArea historyArea;

    private JComboBox<String> passageBox;
    private JComboBox<String> seatCountBox;

    private JTextField[] nameFields;
    private JTextField[] symbolFields;
    private JComboBox<String>[] styleBoxes;
    private JComboBox<String>[] keyboardBoxes;
    private JComboBox<String>[] colourBoxes;
    private JComboBox<String>[] accessoryBoxes;

    private JCheckBox autocorrectBox;
    private JCheckBox caffeineBox;
    private JCheckBox nightShiftBox;

    private JButton startButton;

    // race data for the GUI version
    private int passageLength;
    private int numberOfTypists;
    private int turnNumber;
    private boolean raceRunning;

    private String[] typistNames;
    private char[] typistSymbols;
    private double[] accuracy;
    private int[] progress;
    private int[] burnoutTurns;
    private int[] mistypes;
    private int[] burnoutCount;
    private int[] points;
    private double[] personalBest;

    private javax.swing.Timer raceTimer;

    // some fixed passages so the user can choose a difficulty
    private String shortPassage = "The quick brown fox jumps over the lazy dog.";
    private String mediumPassage = "Typing quickly is useful, but accuracy matters too.";
    private String longPassage = "A typing race is won by staying accurate and recovering quickly from mistakes.";

    public void startRaceGUI()
    {
        // main window setup
        frame = new JFrame("Typing Race Simulator");
        frame.setSize(950, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // top section has all the settings before the race starts
        JPanel setupPanel = new JPanel(new GridLayout(10, 1));

        JPanel basicPanel = new JPanel(new FlowLayout());
        basicPanel.add(new JLabel("Passage:"));
        passageBox = new JComboBox<String>(new String[] {"Short", "Medium", "Long"});
        basicPanel.add(passageBox);

        basicPanel.add(new JLabel("Typists:"));
        seatCountBox = new JComboBox<String>(new String[] {"2", "3", "4", "5", "6"});
        basicPanel.add(seatCountBox);

        setupPanel.add(basicPanel);

        // arrays are used because there can be up to 6 typists in the GUI
        nameFields = new JTextField[6];
        symbolFields = new JTextField[6];
        styleBoxes = new JComboBox[6];
        keyboardBoxes = new JComboBox[6];
        colourBoxes = new JComboBox[6];
        accessoryBoxes = new JComboBox[6];

        int i = 0;
        while (i < 6)
        {
            JPanel typistPanel = new JPanel(new FlowLayout());

            typistPanel.add(new JLabel("Typist " + (i + 1) + ":"));

            nameFields[i] = new JTextField("Typist" + (i + 1), 8);
            typistPanel.add(nameFields[i]);

            symbolFields[i] = new JTextField("" + (i + 1), 2);
            typistPanel.add(symbolFields[i]);

            styleBoxes[i] = new JComboBox<String>(new String[] {"Touch Typist", "Hunt and Peck", "Phone Thumbs"});
            typistPanel.add(styleBoxes[i]);

            keyboardBoxes[i] = new JComboBox<String>(new String[] {"Mechanical", "Membrane", "Touchscreen"});
            typistPanel.add(keyboardBoxes[i]);

            colourBoxes[i] = new JComboBox<String>(new String[] {"Blue", "Red", "Green", "Purple"});
            typistPanel.add(colourBoxes[i]);

            accessoryBoxes[i] = new JComboBox<String>(new String[] {"None", "Wrist Support", "Energy Drink", "Noise-Cancelling"});
            typistPanel.add(accessoryBoxes[i]);

            setupPanel.add(typistPanel);
            i = i + 1;
        }

        JPanel modifierPanel = new JPanel(new FlowLayout());
        autocorrectBox = new JCheckBox("Autocorrect");
        caffeineBox = new JCheckBox("Caffeine Mode");
        nightShiftBox = new JCheckBox("Night Shift");

        modifierPanel.add(autocorrectBox);
        modifierPanel.add(caffeineBox);
        modifierPanel.add(nightShiftBox);

        setupPanel.add(modifierPanel);

        startButton = new JButton("Start Race");
        setupPanel.add(startButton);

        frame.add(setupPanel, BorderLayout.NORTH);

        // output area in the middle
        raceArea = new JTextArea();
        raceArea.setEditable(false);
        raceArea.setLineWrap(true);
        raceArea.setWrapStyleWord(true);
        frame.add(new JScrollPane(raceArea), BorderLayout.CENTER);

        // history/leaderboard area at the bottom
        historyArea = new JTextArea(8, 30);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        frame.add(new JScrollPane(historyArea), BorderLayout.SOUTH);

        // when button is clicked, start a new race
        startButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startNewRace();
            }
        });

        frame.setVisible(true);
    }

    private void startNewRace()
    {
        String passageChoice = (String) passageBox.getSelectedItem();

        if (passageChoice.equals("Short"))
        {
            passageLength = shortPassage.length();
        }
        else if (passageChoice.equals("Medium"))
        {
            passageLength = mediumPassage.length();
        }
        else
        {
            passageLength = longPassage.length();
        }

        numberOfTypists = Integer.parseInt((String) seatCountBox.getSelectedItem());
        turnNumber = 0;
        raceRunning = true;

        typistNames = new String[numberOfTypists];
        typistSymbols = new char[numberOfTypists];
        accuracy = new double[numberOfTypists];
        progress = new int[numberOfTypists];
        burnoutTurns = new int[numberOfTypists];
        mistypes = new int[numberOfTypists];
        burnoutCount = new int[numberOfTypists];

        // these arrays stay between races so the leaderboard can build up
        if (points == null || points.length != 6)
        {
            points = new int[6];
            personalBest = new double[6];
        }

        int i = 0;
        while (i < numberOfTypists)
        {
            typistNames[i] = nameFields[i].getText();

            // if the symbol box is empty, use a default symbol
            if (symbolFields[i].getText().length() > 0)
            {
                typistSymbols[i] = symbolFields[i].getText().charAt(0);
            }
            else
            {
                typistSymbols[i] = '?';
            }

            accuracy[i] = workOutAccuracy(i);
            progress[i] = 0;
            burnoutTurns[i] = 0;
            mistypes[i] = 0;
            burnoutCount[i] = 0;

            i = i + 1;
        }

        raceArea.setText("");
        printGuiRace();

        // stop old timer first if one was already running
        if (raceTimer != null)
        {
            raceTimer.stop();
        }

        raceTimer = new javax.swing.Timer(250, new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                runOneGuiTurn();
            }
        });

        raceTimer.start();
    }

    private double workOutAccuracy(int index)
    {
        double value = 0.70;

        String style = (String) styleBoxes[index].getSelectedItem();
        String keyboard = (String) keyboardBoxes[index].getSelectedItem();
        String accessory = (String) accessoryBoxes[index].getSelectedItem();

        // simple effects for custom typist choices
        if (style.equals("Touch Typist"))
        {
            value = value + 0.15;
        }
        else if (style.equals("Hunt and Peck"))
        {
            value = value - 0.10;
        }
        else
        {
            value = value - 0.05;
        }

        if (keyboard.equals("Mechanical"))
        {
            value = value + 0.05;
        }
        else if (keyboard.equals("Touchscreen"))
        {
            value = value - 0.08;
        }

        if (accessory.equals("Wrist Support"))
        {
            value = value + 0.03;
        }
        else if (accessory.equals("Noise-Cancelling"))
        {
            value = value + 0.02;
        }

        if (nightShiftBox.isSelected())
        {
            value = value - 0.05;
        }

        // keep accuracy inside the normal range
        if (value < 0.0)
        {
            value = 0.0;
        }
        else if (value > 1.0)
        {
            value = 1.0;
        }

        return value;
    }

    private void runOneGuiTurn()
    {
        if (raceRunning == false)
        {
            return;
        }

        turnNumber = turnNumber + 1;

        int i = 0;
        while (i < numberOfTypists)
        {
            moveGuiTypist(i);
            i = i + 1;
        }

        printGuiRace();

        int winner = getGuiWinner();
        if (winner != -1)
        {
            finishGuiRace(winner);
        }
    }

    private void moveGuiTypist(int index)
    {
        if (burnoutTurns[index] > 0)
        {
            burnoutTurns[index] = burnoutTurns[index] - 1;
            return;
        }

        double speedBonus = 0.0;
        String accessory = (String) accessoryBoxes[index].getSelectedItem();

        if (caffeineBox.isSelected() && turnNumber <= 10)
        {
            speedBonus = speedBonus + 0.15;
        }

        if (accessory.equals("Energy Drink") && progress[index] < passageLength / 2)
        {
            speedBonus = speedBonus + 0.10;
        }

        if (Math.random() < accuracy[index] + speedBonus)
        {
            progress[index] = progress[index] + 1;
        }

        double mistakeChance = (1.0 - accuracy[index]) * 0.25;

        if (autocorrectBox.isSelected())
        {
            mistakeChance = mistakeChance / 2.0;
        }

        if (Math.random() < mistakeChance)
        {
            mistypes[index] = mistypes[index] + 1;

            if (autocorrectBox.isSelected())
            {
                progress[index] = progress[index] - 1;
            }
            else
            {
                progress[index] = progress[index] - 2;
            }

            if (progress[index] < 0)
            {
                progress[index] = 0;
            }
        }

        // small burnout chance, mostly for faster/more accurate typists
        if (Math.random() < 0.03 * accuracy[index])
        {
            burnoutTurns[index] = 3;
            burnoutCount[index] = burnoutCount[index] + 1;
        }
    }

    private int getGuiWinner()
    {
        int i = 0;
        while (i < numberOfTypists)
        {
            if (progress[i] >= passageLength)
            {
                return i;
            }

            i = i + 1;
        }

        return -1;
    }

    private void printGuiRace()
    {
        String text = "Typing Race GUI\n";
        text = text + "Passage length: " + passageLength + " characters\n";
        text = text + "Turn: " + turnNumber + "\n\n";

        int i = 0;
        while (i < numberOfTypists)
        {
            text = text + typistNames[i] + " " + typistSymbols[i] + " |";

            int barProgress = progress[i];
            if (barProgress > passageLength)
            {
                barProgress = passageLength;
            }

            int j = 0;
            while (j < passageLength)
            {
                if (j < barProgress)
                {
                    text = text + "=";
                }
                else
                {
                    text = text + " ";
                }

                j = j + 1;
            }

            text = text + "| " + progress[i] + "/" + passageLength;

            if (burnoutTurns[i] > 0)
            {
                text = text + "  BURNT OUT (" + burnoutTurns[i] + ")";
            }

            text = text + "\n";
            i = i + 1;
        }

        raceArea.setText(text);
    }

    private void finishGuiRace(int winner)
    {
        raceRunning = false;
        raceTimer.stop();

        // give simple points based on winning
        points[winner] = points[winner] + 3;

        String results = raceArea.getText();
        results = results + "\nWinner: " + typistNames[winner] + "\n\n";
        results = results + "Results and statistics:\n";

        int i = 0;
        while (i < numberOfTypists)
        {
            double minutes = (turnNumber * 0.25) / 60.0;
            double wpm = 0.0;

            if (minutes > 0)
            {
                wpm = (progress[i] / 5.0) / minutes;
            }

            if (wpm > personalBest[i])
            {
                personalBest[i] = wpm;
            }

            double accuracyPercent = accuracy[i] * 100.0;

            results = results + typistNames[i]
                + " | WPM: " + (int) wpm
                + " | Accuracy: " + (int) accuracyPercent + "%"
                + " | Mistypes: " + mistypes[i]
                + " | Burnouts: " + burnoutCount[i]
                + " | Best WPM: " + (int) personalBest[i]
                + "\n";

            i = i + 1;
        }

        raceArea.setText(results);
        updateHistory(winner);
    }

    private void updateHistory(int winner)
    {
        String text = historyArea.getText();
        text = text + "Race finished. Winner: " + typistNames[winner] + "\n";
        text = text + "Leaderboard points:\n";

        int i = 0;
        while (i < numberOfTypists)
        {
            text = text + typistNames[i] + ": " + points[i] + " points\n";
            i = i + 1;
        }

        if (numberOfTypists >= 2)
        {
            text = text + "Comparison: " + typistNames[0] + " vs " + typistNames[1]
                + " -> " + progress[0] + " chars vs " + progress[1] + " chars\n";
        }

        text = text + "\n";
        historyArea.setText(text);
    }

    public static void main(String[] args)
    {
        TypingRaceGUI gui = new TypingRaceGUI();
        gui.startRaceGUI();
    }
}
