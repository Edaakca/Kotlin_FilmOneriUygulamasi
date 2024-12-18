from fastapi import FastAPI
from pydantic import BaseModel
from transformers import pipeline


app = FastAPI()

model_name = "savasy/bert-base-turkish-sentiment-cased"

model = pipeline("sentiment-analysis", model=model_name)

class ReviewRequest(BaseModel):
    content: str

@app.get("/")
async def root():
    return {"message": "API çalışıyor!"}

@app.post("/analyze")
async def analyze(review: ReviewRequest):
    result = model(review.content)
    return {"result": result}

