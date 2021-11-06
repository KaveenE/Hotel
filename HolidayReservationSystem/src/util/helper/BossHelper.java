/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * A utility <strong>Singleton</strong> class for the following use cases:
 * <ul>
 * <li>Constants used in entity classes
 * <li>Using a singleton <code>Scanner</code> with convenience methods
 * <li>Deal with the shitty <code>java.util.Date</code> API
 * <li>Help with printing messages from <code>Bean Validation</code>
 * <li>This list is non-exhaustive as the number of kickass convenience methods
 * is alot(jk)
 * </ul>
 *
 * @author enkav
 */
public final class BossHelper {

    //Singleton class to use 1 scanner
    private static final Scanner scanner = new Scanner(System.in);

    //For java.time stuff
    private LocalDate properDate1;
    private LocalDate properDate2;

    private static BossHelper singleton;

    private BossHelper() {
    }

    public static BossHelper getSingleton() {
        if (singleton == null) {
            singleton = new BossHelper();
        }

        return singleton;
    }

    public Integer nextInt() {
        return Integer.valueOf(nextLine());
    }

    public Long nextLong() {
        return Long.valueOf(nextLine());
    }

    public Double nextDouble() {
        return Double.valueOf(nextLine());
    }

    public BigDecimal nextBigDecimal() {
        return BigDecimal.valueOf(nextDouble());
    }

    public String nextLine() {
        return scanner.nextLine().trim();

    }

    public Boolean nextBoolean() {
        return Boolean.valueOf(nextLine());
    }

    //util.Date is shit API. People use java.time API
    //But i guess got to use for JPA?
    public static LocalDate dateToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date localDatetoDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static XMLGregorianCalendar DateToXMLDate(Date date) throws DatatypeConfigurationException{
        GregorianCalendar time = new GregorianCalendar();
        time.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(time);
    }
    
    public static LocalDate XMLDateToLocalDate(XMLGregorianCalendar XMLDate) throws DatatypeConfigurationException {
        return dateToLocalDate(XMLDate.toGregorianCalendar().getTime());
    }

    //I'm creating this method so we only compare the date portion in case the time portion fucks us up
    public static int compare(Date date1, Date date2) {

        LocalDate properDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate properDate2 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return properDate1.compareTo(properDate2);

    }

}
