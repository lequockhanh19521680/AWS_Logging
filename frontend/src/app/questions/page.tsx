import { getQuestions } from "../../services/api";

export default async function QuestionsPage() {
  const page = await getQuestions();
  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold">Questions</h1>
      <ul>
        {page.content.map((q: any) => (
          <li key={q.id}>{q.title}</li>
        ))}
      </ul>
    </div>
  );
}

