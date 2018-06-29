package lambda.part3.exercise;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import lambda.data.Employee;
import lambda.data.JobHistoryEntry;
import lambda.data.Person;
import org.junit.Test;

@SuppressWarnings({"unused", "ConstantConditions"})
public class Exercise4 {


    private static List<Integer> calcCodes(String... strings) {
        List<Integer> codes = new ArrayList<>();
        for (String string : strings) {
            for (char letter : string.toCharArray()) {
                codes.add((int) letter);
            }
        }
        return codes;
    }

    private static List<Employee> getEmployees() {
        return Arrays.asList(
            new Employee(
                new Person("Иван", "Мельников", 30),
                Arrays.asList(
                    new JobHistoryEntry(2, "dev", "EPAM"),
                    new JobHistoryEntry(1, "dev", "google")
                )),
            new Employee(
                new Person("Александр", "Дементьев", 28),
                Arrays.asList(
                    new JobHistoryEntry(1, "tester", "EPAM"),
                    new JobHistoryEntry(1, "dev", "EPAM"),
                    new JobHistoryEntry(1, "dev", "google")
                )),
            new Employee(
                new Person("Дмитрий", "Осинов", 40),
                Arrays.asList(
                    new JobHistoryEntry(3, "QA", "yandex"),
                    new JobHistoryEntry(1, "QA", "mail.ru"),
                    new JobHistoryEntry(1, "dev", "mail.ru")
                )),
            new Employee(
                new Person("Анна", "Светличная", 21),
                Collections.singletonList(
                    new JobHistoryEntry(1, "tester", "T-Systems")
                )),
            new Employee(
                new Person("Игорь", "Толмачёв", 50),
                Arrays.asList(
                    new JobHistoryEntry(5, "tester", "EPAM"),
                    new JobHistoryEntry(6, "QA", "EPAM")
                )),
            new Employee(
                new Person("Иван", "Александров", 33),
                Arrays.asList(
                    new JobHistoryEntry(2, "QA", "T-Systems"),
                    new JobHistoryEntry(3, "QA", "EPAM"),
                    new JobHistoryEntry(1, "dev", "EPAM")
                ))
        );
    }

    @Test
    public void mapEmployeesToCodesOfLetterTheirPositionsUsingLazyFlatMapHelper() {
        List<Employee> employees = getEmployees();

        List<Integer> codes = LazyCollectionHelper.from(employees)
            .flatMap(Employee::getJobHistory)
            .map(JobHistoryEntry::getPosition)
            .flatMap(string -> {
                List<Character> result = new ArrayList<>();
                for (char c : string.toCharArray()) {
                    result.add(c);
                }
                return result;
            })
            .map(character -> (int) character)
            .force();
        // TODO              LazyCollectionHelper.from(employees)
        // TODO                                  .flatMap(Employee -> JobHistoryEntry)
        // TODO                                  .map(JobHistoryEntry -> String(position))
        // TODO                                  .flatMap(String -> Character(letter))
        // TODO                                  .map(Character -> Integer(code letter)
        // TODO                                  .force();
        assertEquals(
            calcCodes("dev", "dev", "tester", "dev", "dev", "QA", "QA", "dev", "tester", "tester",
                "QA", "QA", "QA", "dev"), codes);
    }

    private static class LazyCollectionHelper<T, R> {

        private final List<T> source;
        private final Function<T, List<R>> flatMapping;

        private LazyCollectionHelper(List<T> source, Function<T, List<R>> flatMapping) {
            this.source = source;
            this.flatMapping = flatMapping;
        }

        public static <T> LazyCollectionHelper<T, T> from(List<T> list) {
            return new LazyCollectionHelper<>(list, Collections::singletonList);
        }

        public <U> LazyCollectionHelper<T, U> flatMap(Function<R, List<U>> flatMapping) {
            return new LazyCollectionHelper<>(source, this.flatMapping.andThen(list -> {
                List<U> result = new ArrayList<>();
                list.forEach(flatMapping.andThen(result::addAll)::apply);
                return result;
            }));
        }

        public <U> LazyCollectionHelper<T, U> map(Function<R, U> mapping) {
            return new LazyCollectionHelper<>(source, flatMapping.andThen(list -> {
                List<U> result = new ArrayList<>();
                list.forEach(mapping.andThen(result::add)::apply);
                return result;
            }));
        }

        public List<R> force() {
            List<R> result = new ArrayList<>();
            source.forEach(flatMapping.andThen(result::addAll)::apply);
            return result;
        }
    }

}
