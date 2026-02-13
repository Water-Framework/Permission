# Generated with Water Generator
# The Goal of feature test is to ensure the correct format of json responses
# If you want to perform functional test please refer to ApiTest
Feature: Check Permission Rest Api Response

  Scenario: Permission CRUD Operations

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url serviceBaseUrl+'/water/permissions'
    # ---- Add entity fields here -----
    And request
    """
      {
        "entityVersion":1,
        "name": 'name',
        "actionIds": 1,
        "entityResourceName":"prova",
        "resourceId":21,
        "roleId":2,
        "userId":0
       }
    """
    # ---------------------------------
    When method POST
    Then status 200
    # ---- Matching required response json ----
    And match response ==
    """
      { "id": #number,
        "entityVersion":1,
        "entityCreateDate":'#number',
        "entityModifyDate":'#number',
        "name": 'name',
        "actionIds": 1,
        "entityResourceName":"prova",
        "resourceId":21,
        "roleId":2,
        "userId":0,
        "categoryIds": #null,
        "tagIds": #null
       }
    """
    * def entityId = response.id
    
    # --------------- UPDATE -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url serviceBaseUrl+'/water/permissions'
    # ---- Add entity fields here -----
    And request
      """
      { "id": #(entityId),
        "entityVersion":1,
        "name": 'updateName',
        "actionIds": 1,
        "entityResourceName":"prova",
        "resourceId":21,
        "roleId":2,
        "userId":0
       }
      """
    # ---------------------------------
    When method PUT
    Then status 200
    # ---- Matching required response json ----
    And match response ==
    """
      { "id": #number,
        "entityVersion":2,
        "entityCreateDate":'#number',
        "entityModifyDate":'#number',
        "name": 'updateName',
        "actionIds": 1,
        "entityResourceName":"prova",
        "resourceId":21,
        "roleId":2,
        "userId":0,
        "categoryIds": #null,
        "tagIds": #null
       }
    """

  # --------------- FIND -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url serviceBaseUrl+'/water/permissions/'+entityId
    # ---------------------------------
    When method GET
    Then status 200
    # ---- Matching required response json ----
    And match response ==
    """
      { "id": #number,
        "entityVersion":2,
        "entityCreateDate":'#number',
        "entityModifyDate":'#number',
        "name": 'updateName',
        "actionIds": 1,
        "entityResourceName":"prova",
        "resourceId":21,
        "roleId":2,
        "userId":0,
        "categoryIds": #null,
        "tagIds": #null
       }
    """

  # --------------- FIND ALL -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url serviceBaseUrl+'/water/permissions'
    When method GET
    Then status 200
    And match response.results contains
    """
    {
      "id": #(entityId),
      "entityVersion":2,
      "entityCreateDate":'#number',
      "entityModifyDate":'#number',
      "name": 'updateName',
      "actionIds": 1,
      "entityResourceName":"prova",
      "resourceId":21,
      "roleId":2,
      "userId":0,
      "categoryIds": #null,
      "tagIds": #null
    }
    """

    # --------------- PERMISSION MAP -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url serviceBaseUrl+'/water/permissions/map'
    When method POST
    And request
      """
      {
      "entityTestResource": [1]
      }
    """
    Then status 500
  
  # --------------- DELETE -----------------------------

    Given header Content-Type = 'application/json'
    And header Accept = 'application/json'
    Given url serviceBaseUrl+'/water/permissions/'+entityId
    When method DELETE
    # 204 because delete response is empty, so the status code is "no content" but is ok
    Then status 204
