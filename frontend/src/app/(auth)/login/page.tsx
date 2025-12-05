"use client";
export default function Login() {
  return (
    <div className="p-6 space-y-4">
      <a className="px-4 py-2 bg-gray-800 text-white" href="http://localhost:8080/oauth2/authorization/google">Login with Google</a>
      <a className="px-4 py-2 bg-gray-800 text-white" href="http://localhost:8080/oauth2/authorization/github">Login with GitHub</a>
    </div>
  );
}

