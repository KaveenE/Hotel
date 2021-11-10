/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.helper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import util.exception.BeanValidationException;
import util.exception.DoesNotExistException;

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
public final class BossHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    //Got these from 3 posts in stackoverflow 
    public static final int NAME_LENGTH = 50;
    public static final int PASSWORD_LENGTH = 128;
    public static final int MOBILEHP_LENGTH = 15;

    //Ease of bean validation
    public static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

    public static <T> Set<T> convertArrayToSet(T[] arr) {

        //We can't do the typical Set.of(...) since not Java 9
        return Arrays.stream(arr)
                .collect((Collectors.toCollection(() -> new HashSet<>())));
    }

    //This method is used for input validation. But looks effective to use for other cases like for em.find too.
    //We wrap the NPE thrown into a more specific exceptions.
    public static <T> T requireNonNull(T obj, DoesNotExistException ex) throws DoesNotExistException {
        T returnObj;
        try {
            returnObj = Objects.requireNonNull(obj, ex.getMessage());
        } catch (NullPointerException npe) {
            throw ex;
        }

        return returnObj;
    }

    public static <T> boolean printValidationErrorsIfAny(T entity) {

        Set<ConstraintViolation<T>> errors = validator.validate(entity);
        if (!errors.isEmpty()) {
            System.out.println("You have violated the following:");
            errors.forEach(error -> System.out.println(error.getMessage()));
        }

//      What we do in client
//      if (printValidationErrorsIfAny(x))  {
//            bufferScreen();
//        }
        return !errors.isEmpty();
    }
    
    public static <T> void throwValidationErrorsIfAny(T entity) throws BeanValidationException {
        Set<ConstraintViolation<T>> errors = validator.validate(entity);
        StringBuilder sb = new StringBuilder();
        
        if(!errors.isEmpty()) {
            errors.forEach(error -> sb.append(error+" \n"));
            throw new BeanValidationException(sb.toString());
        }
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

    //I'm creating this method so we only compare the date portion in case the time portion fucks us up
    public static int compare(Date date1, Date date2) {

        LocalDate properDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate properDate2 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return properDate1.compareTo(properDate2);

    }

}
