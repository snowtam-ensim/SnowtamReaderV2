package snowtam_ensim.snowtamreader2.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Paul-Etienne FRANCOIS on 01/12/16.
 */
public class Snowtam implements Parcelable {

    private String oaci;
    private String rawContent;
    private String parsedContent;

    public Snowtam(String oaci, String content) {
        this.oaci = oaci;
        this.rawContent = content;

        Log.d("SnowtamReader", rawContent);

        parseSnowtam();
    }

    public String getOaci() {
        return oaci;
    }

    public String getRawContent() {
        return rawContent;
    }

    public String getParsedContent() {
        return parsedContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.oaci);
        dest.writeString(this.rawContent);
        dest.writeString(this.parsedContent);
    }

    protected Snowtam(Parcel in) {
        this.oaci = in.readString();
        this.rawContent = in.readString();
        this.parsedContent = in.readString();
    }

    public static final Creator<Snowtam> CREATOR = new Creator<Snowtam>() {
        @Override
        public Snowtam createFromParcel(Parcel source) {
            return new Snowtam(source);
        }

        @Override
        public Snowtam[] newArray(int size) {
            return new Snowtam[size];
        }
    };

    private void parseSnowtam() {
        String regex = "^(SWEN[0-9]{4}).*\\n\\(SNOWTAM [0-9]{4}\\n(A\\) [A-Z]{4}\\n)B\\) ([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rawContent);

        // Analyze the first part of the SNOWTAM
        if (matcher.find()) {
            // Format the date
            String date = matcher.group(4);
            switch (matcher.group(3)) {
                case "01" :
                    date += " JANUARY AT ";
                    break;
                case "02" :
                    date += " FEBRUARY AT ";
                    break;
                case "03" :
                    date += " MARCH AT ";
                    break;
                case "04" :
                    date += " APRIL AT ";
                    break;
                case "05" :
                    date += " MAY AT ";
                    break;
                case "06" :
                    date += " JUNE AT ";
                    break;
                case "07" :
                    date += " JULY AT ";
                    break;
                case "08" :
                    date += " AUGUST AT ";
                    break;
                case "09" :
                    date += " SEPTEMBER AT ";
                    break;
                case "10" :
                    date += " OCTOBER AT ";
                    break;
                case "11" :
                    date += " NOVEMBER AT ";
                    break;
                case "12" :
                    date += " DECEMBER AT ";
                    break;
            }
            date += matcher.group(5) + "h" + matcher.group(6) + "UTC\n";

            parsedContent = matcher.group(1) + "\n" + matcher.group(2) + "B) " + date;
        }

        // Analyze the second part
        regex = "C\\)\\s*([0-9]+(L|R)?)(\\s|\\n)F\\)\\s*([0-9]+)\\/([0-9]+)\\/([0-9]+)(\\s|\\n)G\\)\\s*([0-9]+|XX)\\/([0-9]+|XX)\\/([0-9]+|XX)(\\s|\\n)" +
                "H\\)\\s*([0-9]+)\\/([0-9]+)\\/([0-9]+)(\\s|\\n)(N\\).*)(\\s|\\n)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(rawContent);

        // Analyze the first part of the SNOWTAM
        while (matcher.find()) {
            parsedContent += "C) ";
            if (matcher.group(1).equals("88")) {
                parsedContent += "ALL RUNWAYS\nF) ";
            }
            else {
                parsedContent += "RUNWAY " + matcher.group(1) + "\nF) ";
            }

            for (int i = 4; i < 7; i++) {
                switch (matcher.group(i)) {
                    case "0" :
                        parsedContent += "CLEAR AND DRY";
                        break;
                    case "1" :
                        parsedContent += "DAMP";
                        break;
                    case "2" :
                        parsedContent += "WET or WATER PATCHES";
                        break;
                    case "3" :
                        parsedContent += "RIME OR FROST COVERED";
                        break;
                    case "4" :
                        parsedContent += "DRY SNOW";
                        break;
                    case "5" :
                        parsedContent += "WET SNOW";
                        break;
                    case "6" :
                        parsedContent += "SLUSH";
                        break;
                    case "7" :
                        parsedContent += "ICE";
                        break;
                    case "8" :
                        parsedContent += "COMPACTED OR ROLLED SNOW";
                        break;
                    case "9" :
                        parsedContent += "FROZEN RUTS OR RIDGES";
                        break;
                }

                if (i < 6) {
                    parsedContent += " / ";
                }
                else {
                    parsedContent += "\nG) MEAN DEPTH : ";
                }
            }

            for (int i = 8; i < 11; i++) {
                if (matcher.group(i).equals("XX")) {
                    parsedContent += "NON SIGNIFICATIVE";
                }
                else {
                    parsedContent += matcher.group(i) + "mm";
                }

                if (i < 10) {
                    parsedContent += " / ";
                }
                else {
                    parsedContent += "\n H) BRAKING ACTION : ";
                }
            }

            for (int i = 12; i < 15; i++) {
                switch (matcher.group(i)) {
                    case "1" :
                        parsedContent += "POOR";
                        break;
                    case "2" :
                        parsedContent += "MEDIUM TO POOR";
                        break;
                    case "3" :
                        parsedContent += "MEDIUM";
                        break;
                    case "4" :
                        parsedContent += "MEDIUM TO GOOD";
                        break;
                    case "5" :
                        parsedContent += "GOOD";
                        break;
                }

                if (i < 14) {
                    parsedContent += " / ";
                }
                else {
                    parsedContent += "\n";
                }
            }

            parsedContent += matcher.group(16);
        }

        regex = ".*N\\).*?\\n([A-Z]\\).*)\\.\\)$";
        pattern = Pattern.compile(regex, Pattern.DOTALL);
        matcher = pattern.matcher(rawContent);

        if (matcher.find()) {
            parsedContent += "\n" + matcher.group(1);
        }
    }

}
