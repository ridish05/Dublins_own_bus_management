import java.io.IOException;
import java.util.*;

public class projectMain
{
    public static final String printStatements =
    		"\t\t\t\tBus Management System For Dublin";

    public static final String options =
            "+-------+----------------------------------------------------------------------------+\n" +
            "| Select An Option And Enter Query Number\n" +
            "|   1   | Get a list of stops between 2 bus stops with cost. \n" +
            "|   2   | Get bus stop details.    \n" +
            "|   3   | Get all trips with a given arrival time.          \n" +
            "|   4   | Quit.\n" +
            "+-------+----------------------------------------------------------------------------+";

    public static void main(String[] args) throws IOException
    {
        boolean runApp = true;
        System.out.println(printStatements);
        Map<String, List<stopTime>> stopTimes = null;

        Scanner scanner = new Scanner(System.in);
        MapData stopMap = null;
        boolean q1RP = false;
        boolean query3RunPrev = false;

        while(runApp)
        {
            System.out.println(options);
            System.out.print("\nEnter A Numeric Value: ");
            String selectedOption = scanner.next();
            
            switch (selectedOption)
            {
                case "1":
                    boolean query1Running = true;
                    if(!q1RP)
                    {
                        stopMap = new MapData("input/stops.txt", "input/stop_times.txt", "input/transfers.txt");
                        q1RP = true;
                    }
                    BusStops searchTree = new BusStops("input/stops.txt");
                    while(query1Running)
                    {   
                        System.out.print("\nEnter the name of the first stop:");
                        String query = scanner.next();
                        query += scanner.nextLine();
                        ArrayList<String> returns = searchTree.queryNameWithReturn(query);
                        if(returns != null) // Check that there was a match
                        {
                            String stopOne = getStop(scanner, returns);
                            System.out.print("Enter the name of the second stop:");
                            query = scanner.next();
                            query += scanner.nextLine();
                            returns = searchTree.queryNameWithReturn(query);
                            if(returns != null)
                            {
                                String stopTwo = getStop(scanner, returns);
                                try
                                {
                                    stopMap.makePaths(stopOne);
                                    Double cost = stopMap.getCost(stopTwo);
                                    if(cost != null)
                                    {
                                        stopMap.getStops(stopTwo, cost);
                                    }
                                    else
                                    {   
                                        System.out.println("No returns Found!");
                                    }
                                }
                                catch(IllegalArgumentException e)
                                {   
                                    System.out.println("No Names Found in Given Data.");
                                }
                            }
                            else
                            {   
                                System.out.println("No Stops Found");
                            }
                        }
                        else
                        {
                            System.out.println("No stops Found");
                        }
                        query1Running = yesNo(scanner, "bus route");
                    }
                break;
                
                case "2":
                    boolean runUserQuery2 = true;
                    while (runUserQuery2)
                    {
                        System.out.print("Please enter the Bus Name: ");
                        String searchQuery = scanner.next();
                        searchQuery += scanner.nextLine();
                        BusStops q2TST = new BusStops("input/stops.txt");
                        int returnValue = q2TST.ourTST.get(searchQuery);
                        if (returnValue >= 0)
                        {
                        	BusStops.printStopNamesMatchingCriteria(q2TST);
                        }
                        else
                        {
                            System.out.println("No returns found!");
                        }
                        runUserQuery2 = yesNo(scanner, "bus stop");
                    }
                break;
                
                case "3":
                    boolean runUserQuery3 = true;
                    if (!query3RunPrev)
                    {
                        stopTimes = stopTime.generateHashMapOfStopTimes("input/stop_times.txt");
                        query3RunPrev = true;
                    }
                    while (runUserQuery3)
                    {
                        System.out.print("Enter arrival time in given format 'hh:mm:ss': ");
                        String userArrivalTimeInput = scanner.next();
                        userArrivalTimeInput = userArrivalTimeInput.trim();
                        if (validTimeFormat(userArrivalTimeInput))
                        {
                            stopTime.findListOfTripsWithGivenArrivalTime(userArrivalTimeInput, stopTimes);
                            runUserQuery3 = yesNo(scanner, "arrival time");
                        }
                        else
                        {
                            System.out.println("Invalid Time Entered!");
                        }
                    }
                break;
                
                case "4":
                    System.out.println("\nThank you for using the Dublin Bus Management System ☺");
                    runApp = false;
                break;
                
                default:
                    System.out.println("Please enter a valid query number.\n");
                break;
            }
        }
        scanner.close();
    }
    
    static private boolean validTimeFormat(String s)
    {
        if (s == null || s.length() != 8)
        {
            return false;
        }
        if (s.charAt(2) == ':' && s.charAt(5) == ':')
        {
            String hh = s.substring(0, 2);
            String mm = s.substring(3, 5);
            String ss = s.substring(6, 8);
            int hours, minutes, seconds;
            try
            {
                hours = Integer.parseInt(hh);
                minutes = Integer.parseInt(mm);
                seconds = Integer.parseInt(ss);
                if (hours > -1 && hours < 24 &&
                    minutes > -1 && minutes < 60 &&
                    seconds > -1 && seconds < 60)
                {
                    return true;
                }
            }
            catch (NumberFormatException nfe)
            {
                return false;
            }
        }
        return false;
    }
    
    private static String getStop(Scanner scanner, ArrayList<String> returns)
    {
        String stop = null;
        if(returns.size() > 1)
        {
            System.out.println("Please Choose 1 of the following: ");
            for(int i = 0; i < returns.size(); i++)
            {
                System.out.println("" + (i + 1) + ". " + returns.get(i));
            }
            boolean firstStopGiven = false;
            while(!firstStopGiven)
            {
                System.out.print("Type in the number of the stop you want to choose: ");
                String s = scanner.next();
                s += scanner.nextLine();
                if(s.matches("[0-9]*")) //If input is some integer
                {
                    int reply = Integer.parseInt(s);
                    if(reply - 1 >= 0 && reply - 1 < returns.size())
                    {
                        stop = returns.get(reply - 1);
                        firstStopGiven = true;
                    }
                    else
                    {
                        System.out.println("Invalid Input: Please choose use one of the numbers found beside the stops listed above");
                    }
                }
                else
                {
                    System.out.println("Invalid Input: Please use numbers only");
                }
            }
        }
        return stop;
    }
    
    private static boolean yesNo(Scanner scanner, String subject)
    {
        boolean userQuery = true;
        boolean exitQuery = true;
        while (exitQuery)
        {
            System.out.print("Do you want to search for another " + subject + "? [Y/N]: ");
            String theReply = scanner.next();
            theReply += scanner.nextLine();
            if (theReply.equalsIgnoreCase("N"))
            {
                exitQuery = false;
                userQuery = false;
            }
            else if (theReply.equalsIgnoreCase("Y"))
            {
                exitQuery = false;
            }
            else
            {   //Error handling
                System.out.println("Invalid Input: Please enter \"Y\" if yes or \"N\" if no");
            }
        }
        return userQuery;
    }
}
