Feature: Planejamento inteligente de alocacao e contingencia de local

  Scenario: Reject negative budget ceiling in analysis
    Given planning allocation setup with a registered event
    When I attempt to analyse venues with ceiling -50
    Then a planning invalid ceiling error occurs

  Scenario: Inactive venue is classified inadequate
    Given planning setup with two venues one inactive
    When I analyse venues with ceiling 2000
    Then the inactive venue candidate has classification INADEQUADO

  Scenario: Unavailability in window makes venue unavailable for principal
    Given planning with event window and two compatible venues
    And unavailability on the principal venue during the window
    When I analyse venues with ceiling 5000
    Then the principal venue is INDISPONIVEL in the analysis

  Scenario: Fix viable principal before confirmation and alert when capacity exceeded
    Given an unconfirmed event with a sufficient large venue
    When I register ceiling 3000 and fix principal from analysis
    And I raise estimated participants above principal capacity
    And I evaluate principal risk
    Then a risk alert exists for the principal

  Scenario: Contingency swap after confirmation records user motive and time
    Given a confirmed event with principal and a viable substitute venue
    When I execute documented swap to substitute by user admin with reason "Contingencia agenda"
    Then history contains one swap with user admin and reason "Contingencia agenda"

  Scenario: Block free principal change after confirmation
    Given a confirmed event with a principal venue
    When I attempt to freely fix another principal venue
    Then a planning already confirmed error occurs
