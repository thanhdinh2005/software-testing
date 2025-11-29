describe('Login E2E Test', () => {
  beforeEach(() => {
    cy.intercept('POST', '/api/auth/login').as('loginRequest');
    cy.visit('http://localhost:3000/');
  })
  afterEach(() => {
    cy.wait(1000); 
  });

  it('Hiện thị form đăng nhập', () => {
    cy.get('[data-testid="username-input"]').should('be.visible');
    cy.get('[data-testid="password-input"]').should('be.visible');
    cy.get('[data-testid="login-button"]').should('be.visible');
  })

  it('Đăng nhập thành công', () => {
    cy.get('[data-testid="username-input"]').type('user1');
    cy.get('[data-testid="password-input"]').type('password1');
    cy.get('[data-testid="login-button"]').click();
    cy.url().should('include', '/dashboard');
    cy.get('[data-testid="username-login"]').should('contain', 'user1');
  })

  it("Đăng nhập thất bại", () => {
    cy.get('[data-testid="username-input"]').type('user');
    cy.get('[data-testid="password-input"]').type('user124');
    cy.get('[data-testid="login-button"]').click();

    cy.get('[data-testid="login-message"]').should('contain', 'Login Failed');
  })

  it("TC1: Hiện thị validation messages", () => {
    cy.get('[data-testid="login-button"]').click();

    cy.get('[data-testid="username-error"]').should('be.visible');
    cy.get('[data-testid="password-error"]').should('be.visible');
  })

  it("TC2: Hiện thị validation messages", () => {
    cy.get('[data-testid="username-input"]').type('admin');
    cy.get('[data-testid="login-button"]').click();

    cy.get('[data-testid="password-error"]').should('be.visible');
  })

  it("TC3: Hiện thị validation messages", () => {
    cy.get('[data-testid="username-input"]').type('ad');
    cy.get('[data-testid="password-input"]').type('userweqew');
    cy.get('[data-testid="login-button"]').click();

    cy.get('[data-testid="username-error"]').should('be.visible');
    cy.get('[data-testid="password-error"]').should('be.visible');
  })

  it("TC4: Hiện ẩn password khi ấn giữ icon", () => {
    cy.get('[data-testid="password-input"]').type('admin123');

    cy.get('[data-testid="password-input"]')
      .should("have.attr", "type", "password");

    cy.get('[data-testid="toggle-password"]').trigger("mousedown");
    cy.wait(500);

    cy.get('[data-testid="password-input"]')
      .should("have.attr", "type", "text");

    cy.get('[data-testid="toggle-password"]').trigger("mouseup");
    cy.wait(500);

    cy.get('[data-testid="password-input"]')
      .should("have.attr", "type", "password");

  })


})