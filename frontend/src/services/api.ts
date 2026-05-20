export type PricingTier = "SILVER" | "GOLD" | "PLATINUM";

export type Seat = {
  id: number;
  rowNumber: number;
  columnNumber: number;
  tier: PricingTier;
  price: number;
  booked: boolean;
};

export type BookingResponse = {
  userName: string;
  seats: Seat[];
  totalPrice: number;
};

type ApiError = {
  messages?: string[];
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers
    },
    ...options
  });

  if (!response.ok) {
    let message = "Something went wrong. Please try again.";

    try {
      const error = (await response.json()) as ApiError;
      if (error.messages?.length) {
        message = error.messages.join(", ");
      }
    } catch {
      message = response.statusText || message;
    }

    throw new Error(message);
  }

  return response.json() as Promise<T>;
}

export function getSeats() {
  return request<Seat[]>("/seats");
}

export function initializeTheater() {
  return request<Seat[]>("/initialize", {
    method: "POST"
  });
}

export function bookSeats(userName: string, seatIds: number[]) {
  return request<BookingResponse>("/book", {
    method: "POST",
    body: JSON.stringify({ userName, seatIds })
  });
}
