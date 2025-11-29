import ProductPage from "../pageObject/ProductPage"

describe('Product E2E Tests', () => {
  const productPage = new ProductPage();

  beforeEach(() => {
    productPage.visit();
  })
  afterEach(() => {
    cy.wait(1000); 
  });

  it('Dang xuat', () => {
    productPage.LogoutButton.click();
  })

  it('Hien thi dung Product DashBoard', () => {
    productPage.topBar.should('be.visible');
    productPage.actionBar.should('be.visible');
    productPage.productList.should('be.visible');
    productPage.getProductInList.should('be.visible');
    productPage.prevButton.should('be.visible');
    productPage.pageTXT.should('be.visible');
    productPage.nextButton.should('be.visible');
  })

  it('Click Next + Prev Button', () =>{
    productPage.nextButton.click();
    productPage.getProductInList.should('be.visible');
    productPage.nextButton.click();
    productPage.getProductInList.should('be.visible');
    productPage.prevButton.click();
    productPage.getProductInList.should('be.visible');
  })

  it('Tao san pham moi thanh cong', () => {
    productPage.CreateButton.click();
    productPage.inputCreateProductForm({
      name: 'Product A',
      price: '15888',
      quantity: '200',
      categoryName: 'BOOKS',
      description: 'Decription Product A'
    });
    productPage.SaveButton.click();

    productPage.toast.should('contain', 'Lưu sản phẩm thành công!')

    productPage.inputSearch.type("Product A");
    productPage.SearchButton.click();

    productPage.getProductInList1('Product A').should('exist');
  })

  it('Cap nhap san pham moi thanh cong', () => {
    productPage.inputSearch.type("Product A");
    productPage.SearchButton.click();
    productPage.getProductInList1('Product A').first().click();
    productPage.EditButton.click();

    productPage.inputName.clear().type('Product B')
    productPage.inputPrice.clear().type('16000');
    productPage.inputCategory.select('OTHER')
    productPage.inputDescription.clear().type('Description Product B');

    productPage.SaveButton.click();
    productPage.toast.should('contain', 'Lưu sản phẩm thành công!')

    productPage.inputSearch.clear().type("Product B");
    productPage.SearchButton.click();

    productPage.getProductInList1('Product B').should('exist');
  })

  it('Xoa san pham thanh cong', () => {
    productPage.getProductInList.first().click();
    productPage.DeleteButton.click();

    productPage.ConfirmDelete.click();
    productPage.toast.should('contain', 'Xóa sản phẩm thành công!');

  })

  it('Click Detail Button -> hien thi form ProductDetail ', () => {
    productPage.detailButton.first().click();
    productPage.nameDetail.should('be.visible');
    productPage.idDetail.should('be.visible');
    productPage.priceDetail.should('be.visible');
    productPage.quantityDetail.should('be.visible');
    productPage.categoryDetail.should('be.visible');
    productPage.descriptionDetail.should('be.visible');
    cy.wait(1500);
    productPage.CloseButton.click();
  })

  it('Search + Filter', () => {
    productPage.inputSearch.clear().type('Sneakers');
    productPage.filterSelectCategory.select('OTHER');
    productPage.inputPriceMin.type("600");
    productPage.inputPriceMax.type("2000");
    productPage.inputQuantityMin.type("28");
    productPage.inputQuantityMax.type("41");
    productPage.SearchButton.click();
    productPage.getProductInList.should('be.visible');
  })


})