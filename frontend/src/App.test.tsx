import { render, screen, waitFor, within } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import App from "./App";
import type { Seat } from "./services/api";

const makeSeats = (): Seat[] =>
  Array.from({ length: 60 }, (_, index) => {
    const id = index + 1;
    const rowNumber = Math.floor(index / 10) + 1;
    const tier = rowNumber <= 2 ? "SILVER" : rowNumber <= 4 ? "GOLD" : "PLATINUM";
    const price = tier === "SILVER" ? 100 : tier === "GOLD" ? 150 : 200;

    return {
      id,
      rowNumber,
      columnNumber: (index % 10) + 1,
      tier,
      price,
      booked: false
    };
  });

function mockFetch(seats = makeSeats()) {
  const fetchMock = vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
    const url = String(input);

    if (url.endsWith("/seats")) {
      return Response.json(seats);
    }

    if (url.endsWith("/initialize")) {
      seats = makeSeats();
      return Response.json(seats);
    }

    if (url.endsWith("/book")) {
      const body = JSON.parse(String(init?.body)) as { userName: string; seatIds: number[] };
      const bookedSeats = body.seatIds.map((seatId) => {
        const seat = seats.find((currentSeat) => currentSeat.id === seatId);
        if (!seat) {
          throw new Error("Invalid test seat");
        }
        seat.booked = true;
        return seat;
      });

      return Response.json(
        {
          userName: body.userName,
          seats: bookedSeats,
          totalPrice: bookedSeats.reduce((total, seat) => total + seat.price, 0)
        },
        { status: 201 }
      );
    }

    return Response.json({ messages: ["Not found"] }, { status: 404 });
  });

  vi.stubGlobal("fetch", fetchMock);
  return fetchMock;
}

describe("App", () => {
  beforeEach(() => {
    mockFetch();
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it("renders all seats with pricing tiers from the API", async () => {
    render(<App />);

    expect(await screen.findAllByRole("gridcell")).toHaveLength(60);
    expect(screen.getByRole("gridcell", { name: /seat 15, row 2, silver, \$100/i })).toBeEnabled();
    expect(screen.getByRole("gridcell", { name: /seat 35, row 4, gold, \$150/i })).toBeEnabled();
    expect(screen.getByRole("gridcell", { name: /seat 55, row 6, platinum, \$200/i })).toBeEnabled();
  });

  it("calculates total price and books selected seats", async () => {
    const user = userEvent.setup();
    const fetchMock = mockFetch();
    render(<App />);

    await user.click(await screen.findByRole("gridcell", { name: /seat 15/i }));
    await user.click(screen.getByRole("gridcell", { name: /seat 35/i }));
    await user.type(screen.getByLabelText(/user name/i), "Ada");

    const summary = screen.getByLabelText(/booking summary/i);
    expect(within(summary).getByText("$250")).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /buy/i }));

    await waitFor(() => {
      expect(screen.getByText(/booked 2 seats for ada\. total: \$250\./i)).toBeInTheDocument();
    });

    expect(fetchMock).toHaveBeenCalledWith(
      "http://localhost:8080/book",
      expect.objectContaining({
        method: "POST",
        body: JSON.stringify({ userName: "Ada", seatIds: [15, 35] })
      })
    );
  });

  it("prevents selecting more than six seats", async () => {
    const user = userEvent.setup();
    render(<App />);

    for (const seatId of [1, 2, 3, 4, 5, 6, 7]) {
      await user.click(await screen.findByRole("gridcell", { name: new RegExp(`seat ${seatId},`, "i") }));
    }

    expect(screen.getByText(/you can select up to 6 seats per booking/i)).toBeInTheDocument();
    expect(screen.getByText("6/6")).toBeInTheDocument();
  });

  it("disables booked seats", async () => {
    const seats = makeSeats();
    seats[0].booked = true;
    mockFetch(seats);
    render(<App />);

    expect(await screen.findByRole("gridcell", { name: /seat 1,/i })).toBeDisabled();
  });
});
