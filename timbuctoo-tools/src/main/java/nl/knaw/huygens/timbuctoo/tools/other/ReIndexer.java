package nl.knaw.huygens.timbuctoo.tools.other;

/*
 * #%L
 * Timbuctoo tools
 * =======
 * Copyright (C) 2012 - 2014 Huygens ING
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.knaw.huygens.timbuctoo.Repository;
import nl.knaw.huygens.timbuctoo.config.Configuration;
import nl.knaw.huygens.timbuctoo.config.TypeNames;
import nl.knaw.huygens.timbuctoo.config.TypeRegistry;
import nl.knaw.huygens.timbuctoo.index.IndexException;
import nl.knaw.huygens.timbuctoo.index.IndexManager;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.storage.StorageIterator;
import nl.knaw.huygens.timbuctoo.tools.config.ToolsInjectionModule;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ReIndexer {

  public final static Logger LOG = LoggerFactory.getLogger(ReIndexer.class);

  public static void main(String[] args) throws ConfigurationException, IndexException, InterruptedException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Configuration config = new Configuration("config.xml");
    Injector injector = Guice.createInjector(new ToolsInjectionModule(config));

    TypeRegistry registry = injector.getInstance(TypeRegistry.class);
    Repository repository = injector.getInstance(Repository.class);
    IndexManager indexManager = injector.getInstance(IndexManager.class);

    try {
      new ReIndexer().indexAsynchrounous(repository, indexManager, registry);
    } finally {
      repository.close();
      indexManager.close();
      stopWatch.stop();
      LOG.info("Time used: {}", stopWatch);
    }
  }

  public void indexAsynchrounous(Repository repository, IndexManager indexManager, TypeRegistry registry) throws InterruptedException, IndexException {
    LOG.info("Clearing index");
    indexManager.deleteAllEntities();
    int numberOfTasks = registry.getPrimitiveDomainEntityTypes().size();
    CountDownLatch countDownLatch = new CountDownLatch(numberOfTasks);
    ExecutorService executor = Executors.newFixedThreadPool(numberOfTasks);

    for (Class<? extends DomainEntity> primitiveType : registry.getPrimitiveDomainEntityTypes()) {
      LOG.info("Task started");
      Runnable indexer = new Indexer(primitiveType, repository, indexManager, countDownLatch);
      executor.execute(indexer);
    }
    executor.shutdown();
    countDownLatch.await(); // wait until all tasks are completed
  }

  protected static class Indexer implements Runnable {

    private final Repository repository;
    private final IndexManager indexManager;
    private final Class<? extends DomainEntity> type;
    private final CountDownLatch countDownLatch;

    public Indexer(Class<? extends DomainEntity> type, Repository repository, IndexManager indexManager, CountDownLatch countDownLatch) {
      this.type = type;
      this.repository = repository;
      this.indexManager = indexManager;
      this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
      String typeName = TypeNames.getInternalName(type);
      LOG.info("Start indexing for {}.", typeName);
      try {
        for (StorageIterator<? extends DomainEntity> iterator = repository.getDomainEntities(type); iterator.hasNext();) {
          indexManager.addEntity(type, iterator.next().getId());
        }
      } catch (RuntimeException e) {
        LOG.error("Error indexing for {}.", typeName);
        LOG.debug("Error: {}", e);
        countDownLatch.countDown();
        throw e;
      } catch (IndexException e) {
        LOG.error("Error indexing for {}.", typeName);
        LOG.debug("Error: {}", e);
      }
      LOG.info("End indexing for {}.", typeName);
      countDownLatch.countDown();
      LOG.info("Threads active: {}", countDownLatch.getCount());
    }
  }

}
