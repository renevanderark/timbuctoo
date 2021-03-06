package nl.knaw.huygens.timbuctoo.tools.importer;

/*
 * #%L
 * Timbuctoo tools
 * =======
 * Copyright (C) 2012 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import static nl.knaw.huygens.timbuctoo.storage.file.LoginCollection.LOGIN_COLLECTION_FILE_NAME;

import java.io.PrintWriter;

import nl.knaw.huygens.timbuctoo.model.Login;
import nl.knaw.huygens.timbuctoo.model.User;
import nl.knaw.huygens.timbuctoo.model.VREAuthorization;
import nl.knaw.huygens.timbuctoo.security.PasswordEncrypter;
import nl.knaw.huygens.timbuctoo.security.UserConfigurationHandler;
import nl.knaw.huygens.timbuctoo.storage.file.JsonFileHandler;
import nl.knaw.huygens.timbuctoo.storage.file.LoginCollection;
import nl.knaw.huygens.timbuctoo.tools.config.ToolsInjectionModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

public class LocalUserImporter extends CSVImporter {
  private static final Logger LOG = LoggerFactory.getLogger("LocalLoginImporter");

  public static void main(String[] args) throws Exception {
    Injector injector = ToolsInjectionModule.createInjectorWithoutSolr();

    JsonFileHandler jsonFileHandler = injector.getInstance(JsonFileHandler.class);
    PasswordEncrypter passwordEncrypter = injector.getInstance(PasswordEncrypter.class);
    UserConfigurationHandler userConfigurationHandler = injector.getInstance(UserConfigurationHandler.class);

    LocalUserImporter localLoginCreator = new LocalUserImporter(jsonFileHandler, passwordEncrypter, userConfigurationHandler);

    if (args.length > 0) {
      localLoginCreator.handleFile(args[0], 9, false);
    } else {
      System.out.println("Please provide an input file as first argument.");
    }
  }

  private final LoginCollection loginCollection;
  private final JsonFileHandler jsonFileHandler;
  private final PasswordEncrypter passwordEncrypter;
  private final UserConfigurationHandler userConfigurationHandler;

  public LocalUserImporter(JsonFileHandler jsonFileHandler, PasswordEncrypter passwordEncrypter, UserConfigurationHandler userConfigurationHandler) throws Exception {
    super(new PrintWriter(System.out));
    this.jsonFileHandler = jsonFileHandler;
    this.passwordEncrypter = passwordEncrypter;
    this.userConfigurationHandler = userConfigurationHandler;
    loginCollection = jsonFileHandler.getCollection(LoginCollection.class, LOGIN_COLLECTION_FILE_NAME);
  }

  @Override
  protected void handleLine(String[] items) throws Exception {
    String userPid = items[0];
    String userName = items[1];
    String password = items[2];
    String givenName = items[3];
    String surname = items[4];
    String emailAddress = items[5];
    String organization = items[6];
    String vreId = items[7];
    String vreRole = items[8];

    addLogin(userPid, userName, password, givenName, surname, emailAddress, organization);

    String userId = addUser(userPid, emailAddress, givenName, surname, organization);

    addVREAuthorization(vreId, userId, vreRole);

    String commonName = createCommonName(givenName, surname);
    LOG.info("Added login and user for \"{}\"", commonName);
    LOG.info("Added VREAuthorization for user \"{}\" and vre \"{}\" with role \"{}\"", commonName, vreId, vreRole);

  }

  private void addLogin(String userPid, String userName, String password, String givenName, String surname, String emailAddress, String organization) throws Exception {
    byte[] salt = passwordEncrypter.createSalt();
    String encryptedPassword = passwordEncrypter.encryptPassword(password, salt);
    Login login = new Login(userPid, userName, encryptedPassword, givenName, surname, emailAddress, organization, salt);

    loginCollection.add(login);
    jsonFileHandler.saveCollection(loginCollection, LOGIN_COLLECTION_FILE_NAME);
  }

  private void addVREAuthorization(String vreId, String userId, String vreRole) throws Exception {
    VREAuthorization vreAuthorization = new VREAuthorization(vreId, userId, vreRole);

    userConfigurationHandler.addVREAuthorization(vreAuthorization);
  }

  private String addUser(String userPid, String emailAddress, String givenName, String surname, String organization) throws Exception {
    User user = new User();
    user.setPersistentId(userPid);
    user.setEmail(emailAddress);
    user.setFirstName(givenName);
    user.setLastName(surname);
    user.setCommonName(createCommonName(givenName, surname));
    user.setOrganisation(organization);

    return userConfigurationHandler.addUser(user);
  }

  private String createCommonName(String givenName, String surname) {
    return String.format("%s %s", givenName, surname);
  }
}
