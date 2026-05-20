import type { Seat } from "../services/api";

type SeatMapProps = {
  seats: Seat[];
  selectedSeatIds: number[];
  onToggleSeat: (seat: Seat) => void;
};

export function SeatMap({ seats, selectedSeatIds, onToggleSeat }: SeatMapProps) {
  return (
    <section className="seat-map-panel" aria-label="Seat map">
      <div className="screen" aria-hidden="true">
        SCREEN
      </div>

      <div className="seat-grid" role="grid" aria-label="Theater seats">
        {seats.map((seat) => {
          const selected = selectedSeatIds.includes(seat.id);
          const className = [
            "seat",
            `seat-${seat.tier.toLowerCase()}`,
            selected ? "seat-selected" : "",
            seat.booked ? "seat-booked" : ""
          ]
            .filter(Boolean)
            .join(" ");

          return (
            <button
              aria-label={`Seat ${seat.id}, row ${seat.rowNumber}, ${seat.tier.toLowerCase()}, $${seat.price}`}
              aria-pressed={selected}
              className={className}
              disabled={seat.booked}
              key={seat.id}
              onClick={() => onToggleSeat(seat)}
              role="gridcell"
              type="button"
            >
              {seat.id}
            </button>
          );
        })}
      </div>
    </section>
  );
}
