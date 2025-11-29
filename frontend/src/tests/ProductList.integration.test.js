import React from "react";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import ProductList from "../components/ProductList";
import { getProducts } from "../services/productService";

jest.mock("../services/productService");

describe("Test ProductList component voi API", () => {
    test("TC1: Hien thi danh sach san pham", async () => {
        getProducts.mockResolvedValue({
            content: [
                { id: 1, name: "Product A", price: 100, quantity: 10, categoryName: "Category 1" },
                { id: 2, name: "Product B", price: 200, quantity: 20, categoryName: "Category 2" },
                { id: 3, name: "Product C", price: 300, quantity: 30, categoryName: "Category 3" },
                { id: 4, name: "Product D", price: 400, quantity: 40, categoryName: "Category 4" }
            ],
            number: 0,
            size: 10,
            totalPages: 1,
            totalElements: 2
        });
        render(<ProductList 
            products={await getProducts().then(data => data.content)} 
            onSelect={() => {}} 
            selectedId={null} 
            onDetail={() => {}} />);

        await waitFor(() => {
            expect(screen.getByText("Product A")).toBeInTheDocument();
            expect(screen.getByText("Product B")).toBeInTheDocument();
            expect(screen.getByText("Product C")).toBeInTheDocument();
            expect(screen.getByText("Product D")).toBeInTheDocument();
        });
    });
  
    test("TC2: Chon san pham se highlight san pham da chon", async () => {
        getProducts.mockResolvedValue({
            content: [
                { id: 1, name: "Product A", price: 100, quantity: 10, categoryName: "Category 1" },
                { id: 2, name: "Product B", price: 200, quantity: 20, categoryName: "Category 2" },
                { id: 3, name: "Product C", price: 300, quantity: 30, categoryName: "Category 3" },
                { id: 4, name: "Product D", price: 400, quantity: 40, categoryName: "Category 4" }
            ],
            number: 0,
            size: 10,
            totalPages: 1,
            totalElements: 2
        });
        const data = await getProducts();

        // Wrapper mo phong Dashboard quan ly selectedId
        const Wrapper = () => {
            const [selectedId, setSelectedId] = React.useState(null);
            const [selectedProduct, setSelectedProduct] = React.useState(null);
            return (
                <ProductList 
                products={data.content} 
                selectedId={selectedId} 
                onSelect={(p) => {setSelectedId(p.id); setSelectedProduct(p);}} 
                onDetail={jest.fn()} 
                />
            );
        };

        const { container } = render(<Wrapper />);
        // Chon dong chua product A
        const rowA = screen.getByText("Product A").closest("tr");
        fireEvent.click(rowA);
        // Kiem tra dong product A duoc highlight
        expect(rowA).toHaveStyle({ background: "rgb(31, 192, 204)" });
        // Kiem tra dong product B khong duoc highlight
        const rowB = screen.getByText("Product B").closest("tr");
        expect(rowB).not.toHaveStyle({ background: "rgb(31, 192, 204)" });
    });

    test("TC3: Click Detail mo dung chi tiet san pham khong trigger onSelect", async () => {
        getProducts.mockResolvedValue({
            content: [
                { id: 1, name: "Product A", price: 100, quantity: 10, categoryName: "Category 1" },
                { id: 2, name: "Product B", price: 200, quantity: 20, categoryName: "Category 2" },
                { id: 3, name: "Product C", price: 300, quantity: 30, categoryName: "Category 3" },
                { id: 4, name: "Product D", price: 400, quantity: 40, categoryName: "Category 4" }
            ],
            number: 0,
            size: 10,
            totalPages: 1,
            totalElements: 2
        });

        const data = await getProducts();

        const onDetail = jest.fn();
        const onSelect = jest.fn();

        render(<ProductList 
            products={data.content} 
            onSelect={onSelect} 
            selectedId={null} 
            onDetail={onDetail} />);
        // Click nut Detail dau tien
        const detailButton = screen.getAllByText("Detail")[0];
        fireEvent.click(detailButton);
        // kiem tra onDetail duoc goi dung voi san pham khong va onSelect khong duoc goi
        expect(onSelect).not.toHaveBeenCalled();
        expect(onDetail).toHaveBeenCalledWith(data.content[0]);

    });
});
