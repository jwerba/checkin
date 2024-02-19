package com.jwerba.checkin.storage;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.jwerba.checkin.model.Day;
import com.jwerba.checkin.model.DayType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FileStorage {

    private Context context;

    public FileStorage(Context context){
        this.context = context;
        try {
            savePreviousDays();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getFilePattern(int year,  int month){
        return String.format("%s_%s_registers.data", year, month);
    }

    private File getFile(String filePattern){
        File appFilesDirectory = context.getFilesDir();
        return new File(appFilesDirectory, filePattern);
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    private LocalDate stringToDate(String s) throws ParseException {
        return LocalDate.parse(s, formatter);
    }
    private String dateToString(LocalDate d) throws ParseException {
        return d.format(formatter);
    }


    public Set<Day> getMonthData(int year, int month) throws IOException, ParseException {
        File file = getFile(getFilePattern(year,  month));
        HashSet<Day> days = new HashSet<>();
        if (!file.exists()){
            return days;
        }

        FileInputStream fileInputStream = new FileInputStream (file);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] splitted = line.split(";");
            String description = "";
            DayType type = DayType.OFFICE_DAY;
            if (splitted.length > 1){
                type = Enum.valueOf(DayType.class, splitted[1]);
            }
            if (splitted.length > 2){
                description = splitted[2];
            }
            LocalDate d = stringToDate(splitted[0]);
            days.add(new Day(d, type, description));
        }
        bufferedReader.close();
        fileInputStream.close();
        return days;
    }

    private void savePreviousDays() throws IOException, ParseException {
        /*add(new Day(LocalDate.of(2023,8,1), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,2), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,4), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,7), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,14), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,21), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,22), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,23), DayType.OFFICE_DAY, ""));
        add(new Day(LocalDate.of(2023,8,24), DayType.OFFICE_DAY, ""));*/
    }

    public void add(Day day) throws IOException, ParseException {
        int year = day.getDate().getYear();
        int month = day.getDate().getMonthValue();
        Set<Day> alreadyRegistered = this.getMonthData(year, month);
        Optional<Day> any = alreadyRegistered.stream().filter(d -> d.getDate().equals(day.getDate())).findAny();
        if (any.isPresent()){
            Day dd = any.get();
            if (day.getDate().equals(dd.getDate()) && day.getDayType() == dd.getDayType() && day.getDescription() != null && day.getDescription().equals(dd.getDescription()))
                return;
            alreadyRegistered.remove(any.get());
        }
        if (day.getDayType() != DayType.REGULAR_DAY){
            alreadyRegistered.add(day);
        }

        File file = getFile(getFilePattern(year,  month));
        if (!file.exists()){
            file.createNewFile();
        }
        StringBuilder sb = new StringBuilder();
        for(Day d: alreadyRegistered){
            String line = dateToString(d.getDate()) + ";" + d.getDayType() + ";" + d.getDescription() + "\n";
            sb.append(line);
        }

        try(FileWriter fw = new FileWriter(file.getAbsolutePath(), false);
            BufferedWriter writer = new BufferedWriter(fw)) {
            writer.write(sb.toString());
        }
    }



}
