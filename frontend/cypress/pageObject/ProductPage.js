class ProductPage {
    visit() {
        cy.visit('http://localhost:3000/');
        cy.get('[data-testid="username-input"]').type('user1');
        cy.get('[data-testid="password-input"]').type('password1');
        cy.get('[data-testid="login-button"]').click();
    }

    getProductInList1(name) {
        return cy.contains('[data-testid="product-item"]', name);
    }

    get getProductInList() {
        return cy.get('[data-testid="product-item"]');
    }

    get inputSearch() {
        return cy.get('[data-testid="search-input"]');
    }

    get filterSelectCategory() {
        return cy.get('[data-testid="filter-category"]');
    }

    get inputPriceMin() {
        return cy.get('[data-testid="price-min-input"]');
    }

    get inputPriceMax() {
        return cy.get('[data-testid="price-max-input"]');
    }

    get inputQuantityMin() {
        return cy.get('[data-testid="quantity-min-input"]');
    }

    get inputQuantityMax() {
        return cy.get('[data-testid="quantity-max-input"]');
    }

    get CreateButton() {
        return cy.get('[data-testid="create-button"]');
    }

    get EditButton() {
        return cy.get('[data-testid="edit-button"]');
    }

    get DeleteButton() {
        return cy.get('[data-testid="delete-button"]');
    }

    get LogoutButton() {
        return cy.get('[data-testid="logout-button"]');
    }

    get SearchButton() {
        return cy.get('[data-testid="search-button"]');
    }

    get topBar() {
        return cy.get('[data-testid="top-bar"]');
    }

    get actionBar() {
        return cy.get('[data-testid="action-bar"]');
    }

    get productList() {
        return cy.get('[data-testid="product-list"]');
    }

    get nextButton() {
        return cy.get('[data-testid="next-button"]');
    }
    get pageTXT() {
        return cy.get('[data-testid="page-txt"]');
    }
    get prevButton() {
        return cy.get('[data-testid="prev-button"]');
    }
    // ProductForm
    inputCreateProductForm(product) {
        cy.get('[data-testid="name"]').type(product.name);
        cy.get('[data-testid="price"]').type(product.price);
        cy.get('[data-testid="quantity"]').type(product.quantity);
        cy.get('[data-testid="category"]').select(product.categoryName);
        cy.get('[data-testid="description"]').type(product.description);
    }

    get inputName() {
        return cy.get('[data-testid="name"]');
    }

    get inputPrice() {
        return cy.get('[data-testid="price"]');
    }

    get inputQuantity() {
        return cy.get('[data-testid="quantity"]');
    }

    get inputCategory() {
        return cy.get('[data-testid="category"]');
    }

    get inputDescription() {
        return cy.get('[data-testid="description"]');
    }

    get SaveButton() {
        return cy.get('[data-testid="Save"]');
    }

    get CancelButton() {
        return cy.get('[data-testid="Cancel"]');
    }
    // ProductDetail
    get detailButton() {
        return cy.get('[data-testid="product-item-detail"]');
    }

    get idDetail() {
        return cy.get('[data-testid="detail-id"]');
    }

    get nameDetail() {
        return cy.get('[data-testid="detail-name"]');
    }

    get priceDetail() {
        return cy.get('[data-testid="detail-price"]');
    }

    get quantityDetail() {
        return cy.get('[data-testid="detail-quantity"]');
    }

    get categoryDetail() {
        return cy.get('[data-testid="detail-category"]');
    }

    get descriptionDetail() {
        return cy.get('[data-testid="detail-description"]');
    }

    get CloseButton() {
        return cy.get('[data-testid="close-button"]');
    }

    get CancelDelete() {
        return cy.get('[data-testid="cancel-delete"]');
    }

    get ConfirmDelete() {
        return cy.get('[data-testid="confirm-delete"]');
    }

    get toast() {
        return cy.get('[data-testid="toast-success"]');
    }
}

export default ProductPage;