"use client";
import { useState } from "react";
import { getProfile, updateProfile } from "../../features/auth/api";

export default function ProfilePage() {
  const [id, setId] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [avatarUrl, setAvatarUrl] = useState("");
  const [bio, setBio] = useState("");

  async function load() {
    if (!id) return;
    const p = await getProfile(id);
    setDisplayName(p.displayName || "");
    setAvatarUrl(p.avatarUrl || "");
    setBio(p.bio || "");
  }

  async function save() {
    if (!id) return;
    await updateProfile(id, { displayName, avatarUrl, bio });
    alert("Saved");
  }

  return (
    <div className="p-6 space-y-4">
      <h1 className="text-xl font-semibold">Profile</h1>
      <input className="border p-2 w-full" placeholder="User ID (UUID)" value={id} onChange={e=>setId(e.target.value)} />
      <button className="px-4 py-2 bg-gray-800 text-white" onClick={load}>Load</button>
      <input className="border p-2 w-full" placeholder="Display Name" value={displayName} onChange={e=>setDisplayName(e.target.value)} />
      <input className="border p-2 w-full" placeholder="Avatar URL" value={avatarUrl} onChange={e=>setAvatarUrl(e.target.value)} />
      <textarea className="border p-2 w-full" placeholder="Bio" value={bio} onChange={e=>setBio(e.target.value)} />
      <button className="px-4 py-2 bg-blue-600 text-white" onClick={save}>Save</button>
    </div>
  );
}

