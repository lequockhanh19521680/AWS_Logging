export default async function LeaderboardPage() {
  const sample = [
    { user: "alice@example.com", points: 1200 },
    { user: "bob@example.com", points: 1050 },
    { user: "charlie@example.com", points: 980 }
  ];
  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold">Leaderboard</h1>
      <ul className="mt-4 space-y-2">
        {sample.map((r, idx) => (
          <li key={idx} className="flex justify-between border p-2 rounded">
            <span>{r.user}</span><span>{r.points} pts</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

