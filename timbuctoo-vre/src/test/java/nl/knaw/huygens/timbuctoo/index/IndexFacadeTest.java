package nl.knaw.huygens.timbuctoo.index;

/*
 * #%L
 * Timbuctoo VRE
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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import nl.knaw.huygens.timbuctoo.Repository;
import nl.knaw.huygens.timbuctoo.model.DomainEntity;
import nl.knaw.huygens.timbuctoo.vre.VRE;
import nl.knaw.huygens.timbuctoo.vre.VRECollection;

import org.junit.Before;
import org.junit.Test;

import test.timbuctoo.index.model.ExplicitlyAnnotatedModel;
import test.timbuctoo.index.model.SubModel;

import com.google.common.collect.Lists;

public class IndexFacadeTest {

  private static final Class<ExplicitlyAnnotatedModel> BASE_TYPE = ExplicitlyAnnotatedModel.class;
  private static final String DEFAULT_ID = "id01234";
  private IndexFacade instance;
  private Repository repositoryMock;
  private static final Class<SubModel> TYPE = SubModel.class;
  private IndexStatus indexStatusMock;
  private VRECollection vreCollectionMock;

  @Before
  public void setUp() {
    indexStatusMock = mock(IndexStatus.class);
    repositoryMock = mock(Repository.class);
    vreCollectionMock = mock(VRECollection.class);
    instance = new IndexFacade(repositoryMock, vreCollectionMock) {
      @Override
      protected IndexStatus createIndexStatus() {
        return indexStatusMock;
      }
    };
  }

  @Test
  public void testAddEntityToOneVRE() throws IndexException, IOException {
    // mock
    VRE vreMock = mock(VRE.class);

    List<ExplicitlyAnnotatedModel> variations = Lists.newArrayList(mock(BASE_TYPE), mock(TYPE));

    // when
    when(repositoryMock.getAllVariations(BASE_TYPE, DEFAULT_ID)).thenReturn(variations);
    setupVREs(vreMock);

    // action
    instance.addEntity(TYPE, DEFAULT_ID);

    // verify
    verify(vreMock).addToIndex(BASE_TYPE, variations);
  }

  @Test
  public void testAddEntityInMultipleVREs() throws IndexException, IOException {
    // mock
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    List<ExplicitlyAnnotatedModel> variations = Lists.newArrayList(mock(ExplicitlyAnnotatedModel.class), mock(SubModel.class));

    // when
    when(repositoryMock.getAllVariations(BASE_TYPE, DEFAULT_ID)).thenReturn(variations);
    setupVREs(vreMock1, vreMock2);

    // action
    instance.addEntity(TYPE, DEFAULT_ID);

    // verify
    verify(vreMock1).addToIndex(BASE_TYPE, variations);
    verify(vreMock2).addToIndex(BASE_TYPE, variations);
  }

  private void setupVREs(VRE... vres) {
    when(vreCollectionMock.getAll()).thenReturn(Lists.newArrayList(vres));
  }

  @Test
  public void testAddEntityStorageManagerReturnsEmptyList() throws IOException, IndexException {
    Class<SubModel> type = SubModel.class;
    Class<ExplicitlyAnnotatedModel> baseType = ExplicitlyAnnotatedModel.class;
    doReturn(Collections.emptyList()).when(repositoryMock).getAllVariations(baseType, DEFAULT_ID);

    try {
      // action
      instance.addEntity(type, DEFAULT_ID);
    } finally {
      // verify
      verify(repositoryMock).getAllVariations(baseType, DEFAULT_ID);
      verifyZeroInteractions(vreCollectionMock);
    }
  }

  @Test(expected = IndexException.class)
  public void testAddToIndexThrowsAnIndexException() throws IOException, IndexException {
    // mock
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    List<ExplicitlyAnnotatedModel> variations = Lists.newArrayList(mock(ExplicitlyAnnotatedModel.class), mock(SubModel.class));

    // when
    when(repositoryMock.getAllVariations(BASE_TYPE, DEFAULT_ID)).thenReturn(variations);
    setupVREs(vreMock1, vreMock2);
    doThrow(IndexException.class).when(vreMock1).addToIndex(BASE_TYPE, variations);

    try {
      // action
      instance.addEntity(TYPE, DEFAULT_ID);
    } finally {
      // verify
      verify(vreMock1).addToIndex(BASE_TYPE, variations);
    }
  }

  @Test
  public void testUpdateEntity() throws IOException, IndexException {
    // mock
    VRE vreMock = mock(VRE.class);
    List<DomainEntity> variations = Lists.newArrayList();
    SubModel model1 = mock(SubModel.class);
    variations.add(model1);

    // when
    doReturn(variations).when(repositoryMock).getAllVariations(BASE_TYPE, DEFAULT_ID);
    setupVREs(vreMock);

    // action
    instance.updateEntity(TYPE, DEFAULT_ID);

    // verify
    verify(vreMock).updateIndex(BASE_TYPE, variations);
  }

  @Test
  public void testDelete() throws IndexException {
    // setup
    VRE vreMock = mock(VRE.class);

    // when
    setupVREs(vreMock);

    // action
    instance.deleteEntity(TYPE, DEFAULT_ID);

    //verify
    verify(vreMock).deleteFromIndex(TYPE, DEFAULT_ID);
  }

  @Test
  public void testDeleteMultipleScopes() throws IndexException {
    // setup
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);

    // action
    instance.deleteEntity(TYPE, DEFAULT_ID);

    //verify
    verify(vreMock1).deleteFromIndex(TYPE, DEFAULT_ID);
    verify(vreMock2).deleteFromIndex(TYPE, DEFAULT_ID);
  }

  @Test(expected = IndexException.class)
  public void testDeleteMultipleScopesFirstThrowsAnException() throws IndexException {
    // setup
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);
    doThrow(IndexException.class).when(vreMock1).deleteFromIndex(TYPE, DEFAULT_ID);

    try {
      // action
      instance.deleteEntity(TYPE, DEFAULT_ID);
    } finally {
      //verify
      verify(vreMock1).deleteFromIndex(TYPE, DEFAULT_ID);
      verifyZeroInteractions(vreMock2);
    }
  }

  @Test
  public void testDeleteEntities() throws IndexException {
    // setup
    VRE vreMock = mock(VRE.class);
    List<String> ids = Lists.newArrayList("id1", "id2", "id3");

    // when
    setupVREs(vreMock);

    // action
    instance.deleteEntities(TYPE, ids);

    // verify
    verify(vreMock).deleteFromIndex(TYPE, ids);
  }

  @Test
  public void testDeleteAllEntities() throws IndexException {
    // setup
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);

    // action
    instance.deleteAllEntities();

    // verify
    verify(vreMock1).clearIndexes();
    verify(vreMock2).clearIndexes();
  }

  @Test(expected = IndexException.class)
  public void testDeleteAllEntitiesVREClearIndexesThrowsAnIndexException() throws IndexException {
    // setup
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);
    doThrow(IndexException.class).when(vreMock1).clearIndexes();

    try {
      // action
      instance.deleteAllEntities();
    } finally {
      // verify
      verify(vreMock1).clearIndexes();
      verifyZeroInteractions(vreMock2);
    }
  }

  @Test
  public void testGetStatus() throws IndexException {
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);

    // action
    IndexStatus actualIndexStatus = instance.getStatus();

    // verify
    verify(vreMock1).addToIndexStatus(indexStatusMock);
    verify(vreMock2).addToIndexStatus(indexStatusMock);

    assertNotNull(actualIndexStatus);
  }

  @Test
  public void testCommitAll() throws IndexException {
    // setup
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);

    // action
    instance.commitAll();

    // verify
    verify(vreMock1).commitAll();
    verify(vreMock2).commitAll();
  }

  @Test(expected = IndexException.class)
  public void testCommitAllFirstThrowsAnIndexException() throws IndexException {
    // setup
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);
    doThrow(IndexException.class).when(vreMock1).commitAll();

    try {
      // action
      instance.commitAll();
    } finally {
      // verify
      verify(vreMock1).commitAll();
      verifyZeroInteractions(vreMock2);
    }
  }

  @Test
  public void testClose() throws IndexException {
    // setup
    VRE vreMock1 = mock(VRE.class);
    VRE vreMock2 = mock(VRE.class);

    setupVREs(vreMock1, vreMock2);

    // action
    instance.close();

    // verify
    verify(vreMock1).close();
    verify(vreMock2).close();
  }

}
