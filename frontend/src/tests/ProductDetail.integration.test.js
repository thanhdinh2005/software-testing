import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import ProductDetail from "../components/ProductDetail";

jest.mock("../services/productService");

describe("Test ProductDetail component", () => {
    test("TC1: Khong co product thi khong hien ProductDetail", () => {
        const { container } = render(<ProductDetail product={null} onClose={jest.fn()} />);

        // Component must return null → container not popup
        expect(container.firstChild).toBeNull();
        });

    test("TC2: Hien thi chi tiet thong tin san pham", () => {
        const product = {
            id: 1,
            name: "Product A",
            price: "500",
            quantity: "10",
            categoryName: "Fashion",
            description: "Description A"
        };

        render(<ProductDetail product={product} onClose={jest.fn()} />);

        expect(screen.getByTestId("detail-id")).toHaveTextContent("1");
        expect(screen.getByTestId("detail-name")).toHaveTextContent("Product A");
        expect(screen.getByTestId("detail-price")).toHaveTextContent("500");
        expect(screen.getByTestId("detail-quantity")).toHaveTextContent("10");
        expect(screen.getByTestId("detail-category")).toHaveTextContent("Fashion");
        expect(screen.getByTestId("detail-description")).toHaveTextContent("Description A");

    });
});
