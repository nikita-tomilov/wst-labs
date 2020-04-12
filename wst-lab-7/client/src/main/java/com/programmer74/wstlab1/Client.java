package com.programmer74.wstlab1;

import com.programmer74.wstlab1.client.service.SQLException_Exception;
import com.programmer74.wstlab1.client.service.User;
import com.programmer74.wstlab1.client.service.UserServiceException;
import com.programmer74.wstlab1.client.service.Users;
import com.programmer74.wstlab1.client.service.UsersService;
import com.programmer74.wstlab1.util.JUDDICommand;
import com.programmer74.wstlab1.util.Mode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.juddi.api_v3.AccessPointType;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.ServiceDetail;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class Client {
  private static JUDDIClient juddiClient;

  private static UsersService userPort;

  private static Mode mode;

  public static void main(String... args) throws SQLException_Exception, IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.println("Enter JUDDI username (juddi-tomcat-3.3.7/conf/tomcat-users.xml)");
    String username = reader.readLine().trim();
    System.out.println("Enter JUDDI user password");
    String password = reader.readLine().trim();
    juddiClient = new JUDDIClient("META-INF/uddi.xml");
    juddiClient.authenticate(username, password);

    mode = Mode.JUDDI;
    processJUDDILoop(reader);
  }

  public static void processServiceLoop(BufferedReader reader) throws SQLException_Exception {
    Command command;
    int currentState = 0;
    UserDTO userDTO;
    Long id;

    writeHelp();
    while (true) {
      try {
        currentState = readState(reader);
        if (currentState < 0 || currentState > Command.values().length) {
          continue;
        } else if (currentState == 0) {
          writeHelp();
          continue;
        }
        command = Command.values()[currentState - 1];
        switch (command) {
          case FIND_ALL:
            userPort.findAll().stream().map(Client::userToString).forEach(System.out::println);
            break;
          case FIND_BY_FILTERS:
            System.out
                .println("Введите значения полей, по которым хотите производить фильтрацию.\n" +
                    "Чтобы не применять фильтр, оставьте значение пустым");
            id = readLong(reader);
            userDTO = readUser(reader);
            userPort.findWithFilters(id, userDTO.getLogin(), userDTO.getPassword(),
                userDTO.getEmail(), userDTO.getGender(), userDTO.getRegisterDate())
                .stream().map(Client::userToString).forEach(System.out::println);
            break;
          case INSERT:
            System.out.println("Введите поля нового пользователя:");
            userDTO = readUser(reader);
            System.out.println("Пользоваетль успешно добавлен. Его id: " + userPort
                .insert(userDTO.getLogin(), userDTO.getPassword(),
                    userDTO.getEmail(), userDTO.getGender(), userDTO.getRegisterDate()));
            break;
          case UPDATE:
            System.out.print("Введите id пользователя, которого хотите изменить: ");
            id = readLong(reader);
            System.out.println("Введите новые поля пользователя");
            userDTO = readUser(reader);
            System.out.println(String.format(
                "Обновлено %s пользователей",
                userPort.update(id, userDTO.getLogin(), userDTO.getPassword(),
                    userDTO.getEmail(),
                    userDTO.getGender(),
                    userDTO.getRegisterDate()
                )
                )
            );
            break;
          case DELETE:
            System.out.print("Введите id пользователя, которого хотите удалить: ");
            id = readLong(reader);
            System.out.println(userPort.delete(id));
            break;
          case QUIT:
            mode = Mode.JUDDI;
              return;
        }
      } catch (UserServiceException e) {
        System.out.println(e.getFaultInfo().getMessage());
        System.out.println("Попробуйте повторить запрос");
      }
    }
  }

  public static void processJUDDILoop(BufferedReader reader) throws SQLException_Exception {
    JUDDICommand command;
    int currentState = 0;
    writeHelp();
    while (true) {
      currentState = readState(reader);
      if (currentState < 0 || currentState > Command.values().length) {
        continue;
      } else if (currentState == 0) {
        writeHelp();
        continue;
      }
      command = JUDDICommand.values()[currentState - 1];
      switch (command) {
        case LIST_BUSINESS:
          listBusinesses(null);
          break;
        case CREATE_BUSINESS:
          System.out.println("Введите имя бизнеса");
          String bn = readString(reader);
          if (bn != null) {
            createBusiness(bn);
          }
          break;
        case CREATE_SERVICE:
          listBusinesses(null);
          String bk;
          do {
            System.out.println("Введите ключ бизнеса");
            bk = readString(reader);
          } while (bk == null);

          String sn;
          do {
            System.out.println("Введите имя сервиса");
            sn = readString(reader);
          } while (sn == null);

          String surl;
          do {
            System.out.println("Введите ссылку на wsdl");
            surl = readString(reader);
          } while (surl == null);
          createService(bk, sn, surl);

          break;
        case FIND_SERVICE:
          System.out.println("Введите имя сервиса для поиска");
          String fsn = readString(reader);
          filterServices(fsn);
          break;
        case USE_SERVICE:
          System.out.println("Введите ключ сервиса");
          String key = readString(reader);
          if (key != null) {
            if (useService(key)) {
              mode = Mode.SERVICE;
              processServiceLoop(reader);
            }
          }
          break;
        case QUIT:
          System.out.println("До свидания!");
          System.exit(0);
      }
    }
  }

  @SneakyThrows
  private static boolean useService(String serviceKey) {

    ServiceDetail serviceDetail = juddiClient.getService(serviceKey.trim());
    if (serviceDetail == null || serviceDetail.getBusinessService() == null || serviceDetail
        .getBusinessService().isEmpty()) {
      System.out.printf("Can not find service by key '%s'\b", serviceKey);
      return false;
    }
    List<BusinessService> services = serviceDetail.getBusinessService();
    BusinessService businessService = services.get(0);
    BindingTemplates bindingTemplates = businessService.getBindingTemplates();
    if (bindingTemplates == null || bindingTemplates.getBindingTemplate().isEmpty()) {
      System.out.printf("No binding template found for service '%s' '%s'\n",
          serviceKey,
          businessService.getBusinessKey());
      return false;
    }
    for (BindingTemplate bindingTemplate : bindingTemplates.getBindingTemplate()) {
      AccessPoint accessPoint = bindingTemplate.getAccessPoint();
      if (accessPoint.getUseType().equals(AccessPointType.END_POINT.toString())) {
        String value = accessPoint.getValue();
        System.out.printf("Using endpoint '%s'\n", value);
        changeEndpointUrl(value);
        return true;
      }
    }
    System.out.printf("No endpoint found for service '%s'\n", serviceKey);
    return false;
  }

  @SneakyThrows
  private static void createService(String businessKey, String serviceName, String wsdlUrl) {
    List<ServiceDetail> serviceDetails = juddiClient
        .publishUrl(businessKey.trim(), serviceName.trim(), wsdlUrl.trim());
    System.out.printf("Services published from wsdl %s\n", wsdlUrl);
    JUDDIUtil.printServicesInfo(serviceDetails.stream()
        .map(ServiceDetail::getBusinessService)
        .flatMap(List::stream)
        .collect(Collectors.toList())
    );
  }

  @SneakyThrows
  public static void createBusiness(String businessName) {
    businessName = businessName.trim();
    BusinessDetail business = juddiClient.createBusiness(businessName);
    System.out.println("New business was created");
    for (BusinessEntity businessEntity : business.getBusinessEntity()) {
      System.out.printf("Key: '%s'\n", businessEntity.getBusinessKey());
      System.out.printf(
          "Name: '%s'\n",
          businessEntity.getName().stream().map(Name::getValue).collect(Collectors.joining(" ")));
    }
  }

  @SneakyThrows
  public static void changeEndpointUrl(String endpointUrl) {
    URL url = new URL(endpointUrl.trim());
    Users usersService = new Users(url);
    userPort = usersService.getUsersServicePort();
    ((BindingProvider) userPort).getRequestContext()
        .put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl.trim());
  }

  @SneakyThrows
  private static void filterServices(String filterArg) {
    List<BusinessService> services = juddiClient.getServices(filterArg);
    JUDDIUtil.printServicesInfo(services);
  }

  @SneakyThrows
  private static void listBusinesses(Void ignored) {
    JUDDIUtil.printBusinessInfo(juddiClient.getBusinessList().getBusinessInfos());
  }

  private static UserDTO readUser(BufferedReader reader) {
    System.out.print("login: ");
    String login = readString(reader);
    System.out.print("password: ");
    String password = readString(reader);
    System.out.print("email: ");
    String email = readString(reader);
    System.out.print("gender: ");
    Boolean gender = readBoolean(reader);
    System.out.print("registerDate(yyyy-mm-dd): ");
    XMLGregorianCalendar registerDate = readDate(reader);
    return new UserDTO(login, password, email, gender, registerDate);
  }

  private static String readString(BufferedReader reader) {
    String trim = null;
    try {
      trim = reader.readLine().trim();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (trim.isEmpty()) {
      return null;
    }
    return trim;
  }

  private static XMLGregorianCalendar readDate(BufferedReader reader) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      Date rd = sdf.parse(reader.readLine());

      GregorianCalendar c = new GregorianCalendar();

      if (rd != null) {
        c.setTime(rd);
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance()
            .newXMLGregorianCalendar(c);
        xmlGregorianCalendar.setTimezone(0);
        return xmlGregorianCalendar;
      } else {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
  }

  private static Long readLong(BufferedReader reader) {
    try {
      return Long.parseLong(reader.readLine());
    } catch (Exception e) {
      return null;
    }
  }

  private static Boolean readBoolean(BufferedReader reader) {
    try {
      String s = reader.readLine();
      if (s.equals("")) {
        return null;
      }
      return Boolean.parseBoolean(s);
    } catch (Exception e) {
      return null;
    }
  }

  private static int readState(BufferedReader reader) {
    try {
      System.out.print("> ");
      String s = reader.readLine();
      return "help".equals(s) ? 0 : Integer.parseInt(s);
    } catch (Exception e) {
      return -1;
    }
  }

  private static void writeHelp() {
    System.out.println("\nВыберите один из пунктов:");
    System.out.println("0. Вывести help");
    if (mode.equals(Mode.SERVICE)) {
      for (Command value : Command.values()) {
        System.out.println(1 + value.ordinal() + ". " + value.getHelp());
      }
    } else if (mode.equals(Mode.JUDDI)) {
      for (JUDDICommand value : JUDDICommand.values()) {
        System.out.println(1 + value.ordinal() + ". " + value.getHelp());
      }
    }
  }

  private static String userToString(User user) {
    return "User{" +
        "id=" + user.getId() +
        ", login='" + user.getLogin() + '\'' +
        ", email='" + user.getEmail() + '\'' +
        ", gender=" + user.isGender() +
        ", registerDate=" + user.getRegisterDate() +
        '}';
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  private static class UserDTO {
    private String login;

    private String password;

    private String email;

    private Boolean gender;

    private XMLGregorianCalendar registerDate;
  }
}
