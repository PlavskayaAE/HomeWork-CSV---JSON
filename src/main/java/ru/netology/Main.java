package ru.netology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {

        String[] one = " 1,John,Smith,USA,25".split(",");
        String[] two = " 2,Inav,Petrov,RU,23".split(",");

        try (CSVWriter writer = new CSVWriter(new FileWriter("data.csv"))) {
            writer.writeNext(one);
            writer.writeNext(two);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        System.out.println(json);
        writeString(json, "data.json");

//********* задача 2 ***********//
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("staff");
        document.appendChild(root);

        Element employee1 = document.createElement("employee");
        createElement(document, employee1, "1", "John", "Smith", "USA", "25");
        root.appendChild(employee1);

        Element employee2 = document.createElement("employee");
        createElement(document, employee2, "2", "Ivan", "Petrov", "RU", "23");
        root.appendChild(employee2);

        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("data.xml"));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(domSource, streamResult);

        List<Employee> newlist = parseXML("data.xml");
        String json2 = listToJson(newlist);
        System.out.println(json2);
        writeString(json2, "data2.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        List<Employee> result = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            result = csv.parse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String listToJson(List<Employee> list) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(list, listType);
    }

    public static void writeString(String json, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createElement(Document document, Element element, String id, String firstName, String lastName, String country, String age) {
        Element setId = document.createElement("id");
        setId.appendChild(document.createTextNode(id));
        element.appendChild(setId);

        Element setFirstName = document.createElement("firstName");
        setFirstName.appendChild(document.createTextNode(firstName));
        element.appendChild(setFirstName);

        Element setLastName = document.createElement("lastName");
        setLastName.appendChild(document.createTextNode(lastName));
        element.appendChild(setLastName);

        Element setCountry = document.createElement("country");
        setCountry.appendChild(document.createTextNode(country));
        element.appendChild(setCountry);

        Element setAge = document.createElement("age");
        setAge.appendChild(document.createTextNode(age));
        element.appendChild(setAge);
    }

    public static List<Employee> parseXML(String name) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> employeeList = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(name));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Map<String, String> forEmploer = new HashMap<>();
            for (int a = 0; a < node.getChildNodes().getLength(); a++) {
                Node nodeChild = node.getChildNodes().item(a);
                String nodeName = nodeChild.getNodeName();
                String nodeValue = nodeChild.getTextContent();
                forEmploer.put(nodeName, nodeValue);

                if (a == node.getChildNodes().getLength() - 1) {
                    long id = Long.parseLong(forEmploer.get("id"));
                    String firstName = forEmploer.get("firstName");
                    String lastName = forEmploer.get("lastName");
                    String country = forEmploer.get("country");
                    int age = Integer.parseInt(forEmploer.get("age"));
                    Employee employee = new Employee(id, firstName, lastName, country, age);
                    employeeList.add(employee);
                }
            }
        }
        return employeeList;
    }
}