import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatches
{
    public static void main( String args[] ){
        String input = "[1463895254]PING www.andi.dz (213.179.181.44) 100(128) bytes of data.[1463895254]108 bytes from 213.179.181.44: icmp_seq=1 ttl=54 time=195 ms[1463895255]108 bytes from 213.179.181.44: icmp_seq=2 ttl=54 time=202 ms[1463895256]108 bytes from 213.179.181.44: icmp_seq=3 ttl=54 time=180 ms[1463895257]108 bytes from 213.179.181.44: icmp_seq=4 ttl=54 time=200 ms[1463895258]108 bytes from 213.179.181.44: icmp_seq=5 ttl=54 time=206 ms[1463895259]108 bytes from 213.179.181.44: icmp_seq=6 ttl=54 time=188 ms[1463895260]108 bytes from 213.179.181.44: icmp_seq=7 ttl=54 time=182 ms[1463895261]108 bytes from 213.179.181.44: icmp_seq=8 ttl=54 time=223 ms\n" +
                "[1463895263]108 bytes from 213.179.181.44: icmp_seq=10 ttl=54 time=199 ms";
        String input2 =
                "[1463895327]PING www.gov.bw (168.167.134.24) 100(128) bytes of data.[1463895327]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=1 ttl=110 time=868 ms[1463895328]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=2 ttl=110 time=892 ms[1463895329]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=3 ttl=110 time=814 ms[1463895330]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=4 ttl=110 time=1009 ms[1463895331]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=5 ttl=110 time=1006 ms[1463895332]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=6 ttl=110 time=984 ms[1463895333]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=7 ttl=110 time=1004 ms[1463895334]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=8 ttl=110 time=1006 ms[1463895335]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=9 ttl=110 time=1013 ms[1463895336]108 bytes from www.gov.bw (168.167.134.24): icmp_seq=10 ttl=110 time=578 ms[1463895336][1463895336]--- www.gov.bw ping statistics ---[1463895336]10 packets transmitted, 10 received, 0% packet loss, time 9007ms[1463895336]rtt min/avg/max/mdev = 578.263/917.875/1013.707/132.095 ms, pipe 2\n";
//        System.out.println(parse1(input));
        try{
            String op[] = parsePingStatisticsMinAvgMaxMdev(input2);
            System.out.println(op[3]);
        }
        catch (RMException e){
            e.printMessage();
        }
    }

    public static String parse1(String input){
        Pattern p =Pattern.compile("\\[([0-9]{10})\\]PING");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            return "error";
    }

    //  Works!
    public static String parseGroupTimestamp(String input) throws TimeStampNotFoundException {
        // Capture the [unix-timestamp] before 'PING'
        Pattern p = Pattern.compile("\\[([0-9]{10})\\]PING");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new TimeStampNotFoundException();
    }

    //  Works!
    public static String parseGroupURL(String input) throws URLNotFoundException {
        // Capture the url after 'PING'
        Pattern p = Pattern.compile("PING\\s+([\\w,\\.]+)\\s+\\(");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new URLNotFoundException();
    }

    //  Works!
    public static String parseGroupIP(String input) throws IPAddressNotFoundException {
        //  Capture the (ip-address) after the url
        Pattern p = Pattern.compile("\\(([0-9,\\.]+)\\)");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new IPAddressNotFoundException();
    }

    // Doesn't Work...
    // patterns seem to be correct but only shows the first value
    public static String[] parseGroupBytes(String input) throws BytesNotFoundException {
        //  Capture the bytes after (ip-address) outside the parenthesis
        //  Capture the bytes after (ip-address) inside the parenthesis
        Pattern p1 = Pattern.compile("\\)\\s+(\\d+)\\(");
        Matcher m1 = p1.matcher(input);
        Pattern p2 = Pattern.compile("\\((\\d+)\\)\\s+bytes");
        Matcher m2 = p2.matcher(input);
        String[] GroupBytes = new String[2];
        int x = m1.groupCount();
        int y = m2.groupCount();
        if(m1.find() || m2.find()){
            GroupBytes[0] = m1.group(1);
            GroupBytes[1] = m2.group(1);
            return GroupBytes;
        }
        else
            throw new BytesNotFoundException();
    }

    // Doesn't Work...
    public static String[] parseIndividualTimestamp(String input) throws TimeStampNotFoundException {
        // Capture the [unix-timestamp] before 'x byes from'
        Pattern p = Pattern.compile("\\[([0-9]{10})\\][\\d]+\\s+bytes");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 1;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(i++);
            }
            return s;
        }
        else
            throw new TimeStampNotFoundException();
    }

    // Doesn't Work...
    public static String[] parseIndividualIP(String input) throws IPAddressNotFoundException {
        // Capture the ip-address after 'x byes from'
        Pattern p = Pattern.compile("bytes\\s+from\\s+([\\d,\\.]+):");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new IPAddressNotFoundException();
    }

    // Doesn't work...
    public static String[] parseIndividualICMP(String input) throws ICMPNotFoundException {
        // Capture the icmp sequence after the individual ip-address
        Pattern p = Pattern.compile("icmp_seq=(\\d+)\\s+ttl");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new ICMPNotFoundException();
    }

    // Doesn't work..
    public static String[] parseIndividualTTL(String input) throws TTLNotFoundException {
        // Capture the individual ttl after the icmp sequence
        Pattern p = Pattern.compile("ttl=(\\d+)\\s+");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new TTLNotFoundException();
    }

    // Doesn't work...
    public static String[] parseIndividualTime(String input) throws TimeNotFoundException {
        // Capture the individual time after ttl
        Pattern p = Pattern.compile("time=(\\d+)\\s+ms");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[10];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new TimeNotFoundException();
    }

    // Doesn't work...
    // No match found
    public static String[] parsePingStatisticsTimestamps(String input) throws TimeStampNotFoundException {
        //  Capture the first [unix-timestamp] mentioned in the ping statistics after 'ping statistics ---'
        //  Capture the second [unix-timestamp] mentioned in the ping statistics before 'rtt'
        Pattern p1 = Pattern.compile("ping\\s+statistics\\s+[\\-]+\\[([0-9]{10})\\]");
        Matcher m1 = p1.matcher(input);
        Pattern p2 = Pattern.compile("ms\\[([0-9]{10})\\]");
        Matcher m2 = p2.matcher(input);
        String[] PSTimestamps = new String[2];

        if(m1.find() || m2.find()){
            PSTimestamps[0] = m1.group(1);
            PSTimestamps[1] = m2.group(1);
            return PSTimestamps;
        }
        else
            throw new TimeStampNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsTransmitted(String input) throws TransmittedNotFoundException {
        // Capture the packets transmitted after the first [unix-timestamp]
        Pattern p = Pattern.compile("\\]([0-9]+)\\s+packets\\s+transmitted");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new TransmittedNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsRecieved(String input) throws ReceivedNotFoundException {
        // Capture the packets received after 'trasnmitted,'
        Pattern p = Pattern.compile("transmitted\\,\\s+([0-9]+)\\s+receivedtransmitted\\,\\s+([0-9]+)\\s+received");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new ReceivedNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsPacketLoss(String input) throws ReceivedNotFoundException {
        // Capture the packet loss % after the 'received,'
        Pattern p = Pattern.compile("loss\\,\\s+time\\s+([0-9]+)ms");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new ReceivedNotFoundException();
    }

    //  Works!
    public static String parsePingStatisticsTime(String input) throws TimeNotFoundException {
        // Capture the time after 'packet loss,'
        Pattern p = Pattern.compile("rtt\\s+min\\/avg\\/max\\/mdev\\s+=\\s+([0-9]+\\.[0-9]+)\\/([0-9]+\\.[0-9]+)\\/([0-9]+\\.[0-9]+)\\/([0-9]+\\.[0-9]+)\\s+ms");
        Matcher m = p.matcher(input);
        if (m.find())
            return m.group(1);
        else
            throw new TimeNotFoundException();
    }

    // Does't work
    // No error but all values null
    public static String[] parsePingStatisticsMinAvgMaxMdev(String input) throws TimeNotFoundException {
        // Capture the rtt min/avg/max/mdev times
        Pattern p = Pattern.compile("rtt\\s+min/avg/max/mdev\\s+=\\s+([0-9]+\\.[0-9]+)/([0-9]+\\.[0-9]+)/([0-9]+\\.[0-9]+)/([0-9]+\\.[0-9]+)\\s+ms");
        Matcher m = p.matcher(input);
        if (m.find()){
            int i = 0;
            String[] s = new  String[4];
            while(m.find()){
                s[i] = m.group(++i);
            }
            return s;
        }
        else
            throw new TimeNotFoundException();

    }
}